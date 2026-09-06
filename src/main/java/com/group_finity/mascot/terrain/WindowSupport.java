package com.group_finity.mascot.terrain;

import java.awt.Point;
import java.util.Optional;

/** Pure calculations for carrying an anchor with a changed window edge. */
public final class WindowSupport {
    private WindowSupport() { }

    public static Optional<Point> carry(Point anchor, WindowEdge oldEdge, WindowTerrain newTerrain) {
        if (oldEdge.type() != EdgeType.TOP || !oldEdge.contains(anchor)) return Optional.empty();
        return newTerrain.edges(EdgeType.TOP).stream()
                .filter(edge -> edge.sourceWindowIdentifier() == oldEdge.sourceWindowIdentifier())
                .findFirst()
                .map(edge -> {
                    long oldWidth = (long) oldEdge.end() - oldEdge.start();
                    double ratio = oldWidth == 0 ? 0 : (anchor.x - oldEdge.start()) / (double) oldWidth;
                    int x = (int) Math.round(edge.start() + ratio * ((long) edge.end() - edge.start()));
                    return new Point(Math.max(edge.start(), Math.min(edge.end(), x)), edge.coordinate());
                });
    }
}
