package com.group_finity.mascot.platform.window;

/**
 * Immutable, platform-neutral rectangular bounds in desktop coordinates.
 */
public record Bounds(int x, int y, int width, int height) {
    public static final Bounds EMPTY = new Bounds(0, 0, 0, 0);

    public Bounds {
        if (width < 0 || height < 0) {
            throw new IllegalArgumentException("Bounds dimensions must not be negative");
        }
    }

    public boolean isEmpty() {
        return width == 0 || height == 0;
    }
}
