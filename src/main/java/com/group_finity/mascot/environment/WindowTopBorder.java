package com.group_finity.mascot.environment;

import com.group_finity.mascot.terrain.WindowEdge;

import java.awt.Point;

/** Adapts an immutable application-window top edge to the existing border API. */
final class WindowTopBorder implements Border {
    private final WindowEdge edge;

    WindowTopBorder(WindowEdge edge) {
        this.edge = edge;
    }

    @Override
    public boolean isOn(Point location) {
        return edge.contains(location);
    }

    @Override
    public Point move(Point location) {
        return location;
    }
}
