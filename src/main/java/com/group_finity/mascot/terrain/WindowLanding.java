package com.group_finity.mascot.terrain;

import java.util.Comparator;
import java.util.Optional;
import java.util.OptionalLong;

/** Pure collision calculations for a falling mascot anchor. */
public final class WindowLanding {
    public record Landing(int y, OptionalLong sourceWindowIdentifier) {
        public boolean isApplicationWindow() {
            return sourceWindowIdentifier.isPresent();
        }
    }

    private WindowLanding() {
    }

    /**
     * Returns the first top edge crossed by a downward movement. Edges at the same
     * height are resolved by their natural ordering, including source identifier.
     */
    public static Optional<WindowEdge> firstCrossedTop(
            int fromX, int fromY, int toX, int toY, WindowTerrain terrain) {
        if (toY <= fromY) return Optional.empty();

        return terrain.edges(EdgeType.TOP).stream()
                .filter(edge -> edge.coordinate() >= fromY && edge.coordinate() <= toY)
                .filter(edge -> {
                    double progress = (edge.coordinate() - fromY) / (double) (toY - fromY);
                    double crossingX = fromX + (toX - fromX) * progress;
                    return crossingX >= edge.start() && crossingX <= edge.end();
                })
                .min(Comparator.comparingInt(WindowEdge::coordinate)
                        .thenComparing(Comparator.naturalOrder()));
    }

    /** Selects a crossed application edge, or the desktop floor when no earlier edge exists. */
    public static Optional<Landing> firstLanding(
            int fromX, int fromY, int toX, int toY, WindowTerrain terrain, int floorY) {
        Optional<WindowEdge> window = firstCrossedTop(fromX, fromY, toX, toY, terrain)
                .filter(edge -> edge.coordinate() <= floorY || floorY < fromY || floorY > toY);
        if (window.isPresent()) {
            WindowEdge edge = window.get();
            return Optional.of(new Landing(edge.coordinate(),
                    OptionalLong.of(edge.sourceWindowIdentifier())));
        }
        if (toY > fromY && floorY >= fromY && floorY <= toY) {
            return Optional.of(new Landing(floorY, OptionalLong.empty()));
        }
        return Optional.empty();
    }
}
