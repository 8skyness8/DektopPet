package com.group_finity.mascot.terrain;

import org.junit.jupiter.api.Test;

import java.awt.Point;
import java.util.List;

import static com.group_finity.mascot.terrain.WindowTerrainRefreshTest.snapshot;
import static org.junit.jupiter.api.Assertions.*;

class WindowInteractionTest {
    @Test void distinguishesSideAndBottomTransitionPreconditions() {
        WindowTerrain terrain = WindowTerrain.fromSnapshots(List.of(snapshot(4, -100, 20, 200, 80)));
        assertTrue(terrain.nearestEdge(new Point(-100, 50), EdgeType.LEFT).orElseThrow()
                .contains(new Point(-100, 50)));
        assertTrue(terrain.nearestEdge(new Point(0, 100), EdgeType.BOTTOM).orElseThrow()
                .contains(new Point(0, 100)));
        assertFalse(terrain.nearestEdge(new Point(0, 100), EdgeType.TOP).orElseThrow()
                .contains(new Point(0, 100)));
    }
}
