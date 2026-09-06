package com.group_finity.mascot.platform.win;

import com.group_finity.mascot.platform.win.jna.User32Extra;
import com.group_finity.mascot.platform.window.Bounds;
import com.group_finity.mascot.platform.window.DesktopWindowInfo;
import com.group_finity.mascot.platform.window.WindowSnapshot;
import com.group_finity.mascot.platform.window.WindowSnapshotMapper;
import com.sun.jna.Pointer;
import com.sun.jna.platform.WindowUtils;
import com.sun.jna.platform.win32.User32;
import com.sun.jna.platform.win32.WinDef.HWND;
import com.sun.jna.platform.win32.WinDef.POINT;
import com.sun.jna.platform.win32.Win32Exception;
import com.sun.jna.platform.win32.WinError;
import com.sun.jna.platform.win32.WinUser.MONITORINFO;

import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;

/** Windows/JNA implementation of platform-neutral desktop and window discovery. */
public final class WindowsDesktopWindowInfo implements DesktopWindowInfo {
    @Override
    public Bounds getDesktopBounds() {
        int x = User32.INSTANCE.GetSystemMetrics(User32.SM_XVIRTUALSCREEN);
        int y = User32.INSTANCE.GetSystemMetrics(User32.SM_YVIRTUALSCREEN);
        return new Bounds(x, y,
                User32.INSTANCE.GetSystemMetrics(User32.SM_CXVIRTUALSCREEN),
                User32.INSTANCE.GetSystemMetrics(User32.SM_CYVIRTUALSCREEN));
    }

    @Override
    public Bounds getWorkAreaBounds() {
        var monitor = User32.INSTANCE.MonitorFromPoint(new POINT.ByValue(0, 0), User32.MONITOR_DEFAULTTOPRIMARY);
        MONITORINFO info = new MONITORINFO();
        if (!User32.INSTANCE.GetMonitorInfo(monitor, info).booleanValue()) {
            return Bounds.EMPTY;
        }
        Rectangle rectangle = info.rcWork.toRectangle();
        return new Bounds(rectangle.x, rectangle.y, rectangle.width, rectangle.height);
    }

    @Override
    public List<WindowSnapshot> getTopLevelWindows() {
        List<WindowSnapshot> snapshots = new ArrayList<>();
        User32.INSTANCE.EnumWindows((window, data) -> {
            WindowSnapshot snapshot = snapshot(window);
            if (snapshot != null) {
                snapshots.add(snapshot);
            }
            return true;
        }, null);
        return List.copyOf(snapshots);
    }

    private static WindowSnapshot snapshot(HWND window) {
        final Rectangle rectangle;
        try {
            rectangle = WindowUtils.getWindowLocationAndSize(window);
        } catch (Win32Exception exception) {
            // A window can close between EnumWindows and reading its bounds.
            if (exception.getHR().intValue() == WinError.E_HANDLE) {
                return null;
            }
            throw exception;
        }
        if (rectangle == null) {
            return null;
        }
        long identifier = Pointer.nativeValue(window.getPointer());
        return WindowSnapshotMapper.map(identifier, rectangle.x, rectangle.y,
                rectangle.x + rectangle.width, rectangle.y + rectangle.height,
                WindowUtils.getWindowTitle(window), User32.INSTANCE.IsWindowVisible(window),
                User32Extra.INSTANCE.IsIconic(window));
    }
}
