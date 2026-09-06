package com.group_finity.mascot.platform.window;

import java.util.Objects;

/**
 * Immutable snapshot of a native top-level window at the time it was enumerated.
 * The native identifier is opaque to callers and is only guaranteed to be stable
 * for the lifetime of the native window.
 */
public record WindowSnapshot(long nativeIdentifier, Bounds bounds, String title,
                             boolean visible, boolean minimized) {
    public WindowSnapshot {
        if (nativeIdentifier == 0) {
            throw new IllegalArgumentException("A native window identifier must not be zero");
        }
        Objects.requireNonNull(bounds, "bounds");
        title = Objects.requireNonNullElse(title, "");
    }
}
