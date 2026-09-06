package com.group_finity.mascot.platform.window;

import java.util.List;
import java.util.Objects;

/**
 * Discovers usable application windows without coupling consumers to native APIs.
 *
 * <p>A usable window is visible, restored, non-empty, not DWM-cloaked, not a tool
 * or owned window, not owned by DesktopPet, not the Windows shell window, and enabled
 * for user interaction. The rules intentionally do not depend on title text because
 * legitimate application windows may have an empty title.</p>
 */
public final class VisibleWindowDiscovery {
    private final DesktopWindowInfo windowInfo;

    public VisibleWindowDiscovery(DesktopWindowInfo windowInfo) {
        this.windowInfo = Objects.requireNonNull(windowInfo, "windowInfo");
    }

    public List<WindowSnapshot> discover() {
        return windowInfo.getTopLevelWindows().stream()
                .filter(VisibleWindowDiscovery::isUsable)
                .toList();
    }

    public static boolean isUsable(WindowSnapshot window) {
        Objects.requireNonNull(window, "window");
        return window.visible()
                && !window.minimized()
                && !window.cloaked()
                && !window.bounds().isEmpty()
                && !window.toolWindow()
                && !window.owned()
                && !window.desktopPetOwned()
                && !window.shellWindow()
                && window.interactive();
    }
}
