package com.group_finity.mascot.platform.window;

import java.util.Objects;

/**
 * Immutable snapshot of a native top-level window at the time it was enumerated.
 * The native identifier is opaque to callers and is only guaranteed to be stable
 * for the lifetime of the native window.
 */
public record WindowSnapshot(long nativeIdentifier, Bounds bounds, String title,
                             boolean visible, boolean minimized, boolean cloaked,
                             boolean toolWindow, boolean owned, boolean desktopPetOwned,
                             boolean shellWindow, boolean interactive) {
    /**
     * Backward-compatible constructor for providers that cannot report Windows-specific
     * eligibility metadata. Such windows are treated as ordinary interactive windows.
     */
    public WindowSnapshot(long nativeIdentifier, Bounds bounds, String title,
                          boolean visible, boolean minimized) {
        this(nativeIdentifier, bounds, title, visible, minimized, false,
                false, false, false, false, true);
    }

    public WindowSnapshot {
        if (nativeIdentifier == 0) {
            throw new IllegalArgumentException("A native window identifier must not be zero");
        }
        Objects.requireNonNull(bounds, "bounds");
        title = Objects.requireNonNullElse(title, "");
    }
}
