package com.group_finity.mascot.terrain;

import com.group_finity.mascot.platform.window.Bounds;
import com.group_finity.mascot.platform.window.VisibleWindowDiscovery;
import com.group_finity.mascot.platform.window.WindowSnapshot;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

/** Immutable terrain generated from eligible window rectangles. */
public final class WindowTerrain {
    public static final WindowTerrain EMPTY = new WindowTerrain(List.of());

    private final List<WindowEdge> edges;

    private WindowTerrain(List<WindowEdge> edges) {
        this.edges = edges.stream().sorted().toList();
    }

    public static WindowTerrain fromSnapshots(Collection<WindowSnapshot> snapshots) {
        List<WindowEdge> edges = new ArrayList<>();
        snapshots.stream().filter(VisibleWindowDiscovery::isUsable).forEach(snapshot -> {
            Bounds bounds = snapshot.bounds();
            int right = Math.addExact(bounds.x(), bounds.width());
            int bottom = Math.addExact(bounds.y(), bounds.height());
            long id = snapshot.nativeIdentifier();
            edges.add(new WindowEdge(id, EdgeType.TOP, bounds.x(), right, bounds.y()));
            edges.add(new WindowEdge(id, EdgeType.BOTTOM, bounds.x(), right, bottom));
            edges.add(new WindowEdge(id, EdgeType.LEFT, bounds.y(), bottom, bounds.x()));
            edges.add(new WindowEdge(id, EdgeType.RIGHT, bounds.y(), bottom, right));
        });
        return edges.isEmpty() ? EMPTY : new WindowTerrain(edges);
    }

    public List<WindowEdge> edges() {
        return edges;
    }

    public List<WindowEdge> edges(EdgeType type) {
        return edges.stream().filter(edge -> edge.type() == type).toList();
    }

    public Optional<WindowEdge> edge(long sourceIdentifier, EdgeType type) {
        return edges(type).stream()
                .filter(edge -> edge.sourceWindowIdentifier() == sourceIdentifier)
                .findFirst();
    }

    /**
     * Finds the closest edge by perpendicular distance, then along-edge distance.
     * The natural edge ordering provides deterministic tie breaking.
     */
    public Optional<WindowEdge> nearestEdge(Point point, EdgeType type) {
        return edges(type).stream().min(Comparator
                .comparingLong((WindowEdge edge) -> perpendicularDistance(edge, point))
                .thenComparingLong(edge -> alongDistance(edge, point))
                .thenComparing(Comparator.naturalOrder()));
    }

    private static long perpendicularDistance(WindowEdge edge, Point point) {
        return Math.abs((long) edge.coordinate() - (edge.isHorizontal() ? point.y : point.x));
    }

    private static long alongDistance(WindowEdge edge, Point point) {
        int value = edge.isHorizontal() ? point.x : point.y;
        if (value < edge.start()) return (long) edge.start() - value;
        if (value > edge.end()) return (long) value - edge.end();
        return 0;
    }
}
