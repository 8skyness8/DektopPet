package com.group_finity.mascot.platform.window;

/**
 * Pure conversion from platform readings to the public snapshot model.
 */
public final class WindowSnapshotMapper {
    private WindowSnapshotMapper() {
    }

    public static WindowSnapshot map(long nativeIdentifier, int left, int top, int right, int bottom,
                                     String title, boolean visible, boolean minimized) {
        return map(nativeIdentifier, left, top, right, bottom, title, visible, minimized,
                false, false, false, false, false, true);
    }

    public static WindowSnapshot map(long nativeIdentifier, int left, int top, int right, int bottom,
                                     String title, boolean visible, boolean minimized, boolean cloaked,
                                     boolean toolWindow, boolean owned, boolean desktopPetOwned,
                                     boolean shellWindow, boolean interactive) {
        return new WindowSnapshot(nativeIdentifier,
                new Bounds(left, top, Math.max(0, right - left), Math.max(0, bottom - top)),
                title, visible, minimized, cloaked, toolWindow, owned, desktopPetOwned,
                shellWindow, interactive);
    }
}
