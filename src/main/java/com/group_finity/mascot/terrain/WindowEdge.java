package com.group_finity.mascot.terrain;

import java.awt.Point;

/**
 * One immutable, platform-neutral edge of a top-level window.
 * End points are inclusive and ordered from left to right or top to bottom.
 */
public record WindowEdge(long sourceWindowIdentifier, EdgeType type,
                         int start, int end, int coordinate)
        implements Comparable<WindowEdge> {
    public WindowEdge {
        if (sourceWindowIdentifier == 0) {
            throw new IllegalArgumentException("A source window identifier must not be zero");
        }
        if (type == null) {
            throw new NullPointerException("type");
        }
        if (end < start) {
            throw new IllegalArgumentException("Edge end must not precede its start");
        }
    }

    public boolean isHorizontal() {
        return type == EdgeType.TOP || type == EdgeType.BOTTOM;
    }

    public boolean contains(Point point) {
        return isHorizontal()
                ? point.y == coordinate && point.x >= start && point.x <= end
                : point.x == coordinate && point.y >= start && point.y <= end;
    }

    /** Returns whether collinear portions of two edges intersect, including at an endpoint. */
    public boolean overlaps(WindowEdge other) {
        return isHorizontal() == other.isHorizontal()
                && coordinate == other.coordinate
                && start <= other.end && other.start <= end;
    }

    @Override
    public int compareTo(WindowEdge other) {
        int result = Integer.compare(coordinate, other.coordinate);
        if (result == 0) result = Integer.compare(start, other.start);
        if (result == 0) result = Integer.compare(end, other.end);
        if (result == 0) result = type.compareTo(other.type);
        if (result == 0) result = Long.compare(sourceWindowIdentifier, other.sourceWindowIdentifier);
        return result;
    }
}
