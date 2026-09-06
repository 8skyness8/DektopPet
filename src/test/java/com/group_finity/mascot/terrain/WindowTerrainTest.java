package com.group_finity.mascot.terrain;

import com.group_finity.mascot.platform.window.Bounds;
import com.group_finity.mascot.platform.window.WindowSnapshot;
import org.junit.jupiter.api.Test;

import java.awt.Point;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class WindowTerrainTest {
    @Test
    void createsAllOrderedEdgesWithNegativeCoordinatesAndSourceIds() {
        WindowTerrain terrain = WindowTerrain.fromSnapshots(List.of(
                snapshot(20, new Bounds(10, 20, 30, 40)),
                snapshot(10, new Bounds(-100, -50, 25, 10))));

        assertEquals(8, terrain.edges().size());
        assertEquals(new WindowEdge(10, EdgeType.TOP, -100, -75, -50),
                terrain.edges(EdgeType.TOP).getFirst());
        assertTrue(terrain.edges(EdgeType.TOP).getFirst().contains(new Point(-80, -50)));
        assertFalse(terrain.edges(EdgeType.TOP).getFirst().contains(new Point(-74, -50)));
    }

    @Test
    void filtersIneligibleSnapshotsBeforeMakingTerrain() {
        WindowSnapshot minimized = new WindowSnapshot(1, new Bounds(0, 0, 10, 10), "", true, true);

        assertSame(WindowTerrain.EMPTY, WindowTerrain.fromSnapshots(List.of(minimized)));
    }

    @Test
    void overlapRequiresCollinearIntersectingEdges() {
        WindowEdge first = new WindowEdge(1, EdgeType.TOP, -10, 10, 5);

        assertTrue(first.overlaps(new WindowEdge(2, EdgeType.BOTTOM, 10, 20, 5)));
        assertFalse(first.overlaps(new WindowEdge(2, EdgeType.TOP, 0, 20, 6)));
        assertFalse(first.overlaps(new WindowEdge(2, EdgeType.LEFT, -10, 10, 5)));
    }

    @Test
    void nearestEdgeUsesDistanceThenDeterministicOrdering() {
        WindowTerrain terrain = WindowTerrain.fromSnapshots(List.of(
                snapshot(2, new Bounds(0, 10, 20, 20)),
                snapshot(1, new Bounds(0, 10, 20, 30))));

        assertEquals(1, terrain.nearestEdge(new Point(10, 0), EdgeType.TOP)
                .orElseThrow().sourceWindowIdentifier());
    }

    private static WindowSnapshot snapshot(long id, Bounds bounds) {
        return new WindowSnapshot(id, bounds, "window", true, false);
    }
}
