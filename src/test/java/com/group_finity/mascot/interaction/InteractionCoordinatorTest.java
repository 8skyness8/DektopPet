package com.group_finity.mascot.interaction;

import org.junit.jupiter.api.Test;

import java.awt.Point;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class InteractionCoordinatorTest {
    private record Pet(int id, Point point) { }

    @Test void selectsNearestAndUsesStableIdForTies() {
        Pet requester = new Pet(9, new Point(0, 0));
        Pet highId = new Pet(3, new Point(2, 0));
        Pet lowId = new Pet(1, new Point(-2, 0));
        assertSame(lowId, InteractionCoordinator.nearest(requester, List.of(highId, requester, lowId),
                Pet::point, Pet::id).orElseThrow());
    }

    @Test void safelyReturnsEmptyAfterParticipantDisappears() {
        Pet requester = new Pet(1, new Point());
        assertTrue(InteractionCoordinator.nearest(requester, List.of(requester), Pet::point, Pet::id).isEmpty());
    }
}
