package com.group_finity.mascot.platform.window;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class WindowModelTest {
    @Test
    void boundsAreImmutableValuesAndSupportNegativeDesktopCoordinates() {
        Bounds bounds = new Bounds(-1920, -100, 1920, 1080);

        assertEquals(-1920, bounds.x());
        assertEquals(-100, bounds.y());
        assertFalse(bounds.isEmpty());
        assertEquals(bounds, new Bounds(-1920, -100, 1920, 1080));
    }

    @Test
    void boundsRejectNegativeDimensions() {
        assertThrows(IllegalArgumentException.class, () -> new Bounds(0, 0, -1, 1));
        assertThrows(IllegalArgumentException.class, () -> new Bounds(0, 0, 1, -1));
    }

    @Test
    void snapshotRequiresAStableNonZeroIdentifier() {
        assertThrows(IllegalArgumentException.class,
                () -> new WindowSnapshot(0, Bounds.EMPTY, "window", true, false));
    }

    @Test
    void snapshotNormalizesMissingTitle() {
        WindowSnapshot snapshot = new WindowSnapshot(42, Bounds.EMPTY, null, false, true);

        assertEquals("", snapshot.title());
        assertTrue(snapshot.minimized());
    }

    @Test
    void genericImplementationSafelyReportsNoNativeWindows() {
        assertEquals(java.util.List.of(), new GenericDesktopWindowInfo().getTopLevelWindows());
    }
}
