package com.group_finity.mascot.platform.window;

import com.group_finity.mascot.platform.win.WindowsDesktopWindowInfo;
import com.sun.jna.Platform;

/** Creates desktop/window information providers without coupling callers to JNA. */
public final class DesktopWindowInfoFactory {
    private DesktopWindowInfoFactory() {
    }

    public static DesktopWindowInfo create() {
        return Platform.isWindows() ? new WindowsDesktopWindowInfo() : new GenericDesktopWindowInfo();
    }
}
