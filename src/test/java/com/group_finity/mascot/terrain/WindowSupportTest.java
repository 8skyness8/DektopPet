package com.group_finity.mascot.terrain;

import org.junit.jupiter.api.Test;

import java.awt.Point;
import java.util.List;

import static com.group_finity.mascot.terrain.WindowTerrainRefreshTest.snapshot;
import static org.junit.jupiter.api.Assertions.*;

class WindowSupportTest {
    @Test void carriesStandingAnchorWithMoveAndResize() {
        WindowEdge oldTop = WindowTerrain.fromSnapshots(List.of(snapshot(8, 100, 200, 100, 60)))
                .edge(8, EdgeType.TOP).orElseThrow();
        WindowTerrain moved = WindowTerrain.fromSnapshots(List.of(snapshot(8, 300, 350, 200, 60)));
        assertEquals(new Point(400, 350), WindowSupport.carry(new Point(150, 200), oldTop, moved).orElseThrow());
    }

    @Test void invalidatesSupportWhenWindowClosesOrBecomesIneligible() {
        WindowEdge oldTop = WindowTerrain.fromSnapshots(List.of(snapshot(8, 100, 200, 100, 60)))
                .edge(8, EdgeType.TOP).orElseThrow();
        assertTrue(WindowSupport.carry(new Point(150, 200), oldTop, WindowTerrain.EMPTY).isEmpty());
        var minimized = new com.group_finity.mascot.platform.window.WindowSnapshot(8,
                new com.group_finity.mascot.platform.window.Bounds(100, 200, 100, 60), "minimized",
                true, true, false, false, false, false, false, true);
        assertTrue(WindowSupport.carry(new Point(150, 200), oldTop,
                WindowTerrain.fromSnapshots(List.of(minimized))).isEmpty());
    }
}
