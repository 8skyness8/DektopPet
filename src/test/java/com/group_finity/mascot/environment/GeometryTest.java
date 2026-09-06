package com.group_finity.mascot.environment;

import org.junit.jupiter.api.Test;

import java.awt.Point;
import java.awt.Rectangle;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class GeometryTest {
    @Test
    void areaTracksBoundsAndMovementDeltas() {
        Area area = new Area();
        area.setRect(10, 20, 30, 40);
        area.setRect(13, 18, 30, 45);

        assertEquals(new Rectangle(13, 18, 30, 45), area.toRectangle());
        assertEquals(3, area.getDleft());
        assertEquals(-2, area.getDtop());
        assertEquals(3, area.getDright());
        assertEquals(3, area.getDbottom());
    }

    @Test
    void areaIncludesItsPointBoundariesButRejectsNegativeDimensions() {
        Area area = new Area();
        area.set(10, 20, 40, 60);

        assertTrue(area.contains(new Point(10, 20)));
        assertTrue(area.contains(40, 60));
        area.set(40, 20, 10, 60);
        assertFalse(area.contains(25, 30));
    }

    @Test
    void locationSmoothsCoordinateDeltasUsingExistingMomentum() {
        Location location = new Location();
        location.set(10, -6);
        location.set(14, -4);

        assertEquals(4, location.getDx());
        assertEquals(0, location.getDy());
        assertEquals(14, location.getX());
        assertEquals(-4, location.getY());
    }
}
