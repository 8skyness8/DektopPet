package com.group_finity.mascot.platform.window;

import java.util.List;

/**
 * Platform-neutral access to desktop geometry and unfiltered top-level windows.
 */
public interface DesktopWindowInfo {
    Bounds getDesktopBounds();

    Bounds getWorkAreaBounds();

    /**
     * Returns all top-level windows reported by the platform. Eligibility and
     * filtering deliberately belong to a later layer.
     */
    List<WindowSnapshot> getTopLevelWindows();
}
