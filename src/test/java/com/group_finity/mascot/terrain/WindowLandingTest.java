package com.group_finity.mascot.terrain;

import com.group_finity.mascot.platform.window.Bounds;
import com.group_finity.mascot.platform.window.WindowSnapshot;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class WindowLandingTest {
    @Test
    void landsOnFirstCrossedTopAndCannotTunnelPastIt() {
        WindowTerrain terrain = terrain(
                snapshot(2, new Bounds(0, 80, 100, 20)),
                snapshot(1, new Bounds(0, 40, 100, 20)));

        WindowLanding.Landing landing = WindowLanding.firstLanding(
                50, 0, 50, 120, terrain, 150).orElseThrow();

        assertEquals(40, landing.y());
        assertEquals(1, landing.sourceWindowIdentifier().orElseThrow());
    }

    @Test
    void diagonalMovementMustCrossWithinHorizontalExtent() {
        WindowTerrain terrain = terrain(snapshot(1, new Bounds(40, 50, 20, 20)));

        assertTrue(WindowLanding.firstCrossedTop(0, 0, 100, 100, terrain).isPresent());
        assertTrue(WindowLanding.firstCrossedTop(100, 0, 120, 100, terrain).isEmpty());
    }

    @Test
    void overlappingTopsUseStableSourceIdentifierAsTieBreaker() {
        WindowTerrain terrain = terrain(
                snapshot(9, new Bounds(-10, 30, 30, 20)),
                snapshot(3, new Bounds(-10, 30, 30, 20)));

        assertEquals(3, WindowLanding.firstCrossedTop(0, 0, 0, 50, terrain)
                .orElseThrow().sourceWindowIdentifier());
    }

    @Test
    void fallsBackToDesktopFloorWithoutEligibleWindow() {
        WindowSnapshot staleMinimized = new WindowSnapshot(
                1, new Bounds(0, 20, 100, 20), "stale", true, true);

        WindowLanding.Landing landing = WindowLanding.firstLanding(
                10, 0, 10, 100, terrain(staleMinimized), 90).orElseThrow();

        assertEquals(90, landing.y());
        assertFalse(landing.isApplicationWindow());
    }

    @Test
    void upwardMovementDoesNotLand() {
        assertTrue(WindowLanding.firstLanding(0, 50, 0, 0,
                WindowTerrain.EMPTY, 100).isEmpty());
    }

    private static WindowTerrain terrain(WindowSnapshot... snapshots) {
        return WindowTerrain.fromSnapshots(List.of(snapshots));
    }

    private static WindowSnapshot snapshot(long id, Bounds bounds) {
        return new WindowSnapshot(id, bounds, "window", true, false);
    }
}
