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

    @Test void claimsExclusivelyCompletesAndAppliesCooldown() {
        Pet first = new Pet(1, new Point(0, 0));
        Pet second = new Pet(2, new Point(1, 0));
        Pet third = new Pet(3, new Point(2, 0));
        InteractionCoordinator<Pet> coordinator = new InteractionCoordinator<>(10, 100);
        assertTrue(coordinator.start(first, List.of(first, second, third), Pet::point, Pet::id, pet -> true, 0).isPresent());
        assertTrue(coordinator.isClaimed(second));
        assertTrue(coordinator.start(third, List.of(first, second, third), Pet::point, Pet::id, pet -> true, 0).isEmpty());
        assertTrue(coordinator.advance(10, pet -> true, pet -> false).isEmpty());
        assertTrue(coordinator.advance(20, pet -> true, pet -> false).isEmpty());
        assertTrue(coordinator.advance(30, pet -> true, pet -> false).isEmpty());
        assertEquals(1, coordinator.advance(40, pet -> true, pet -> false).size());
        assertEquals(0, coordinator.activeSessionCount());
        assertTrue(coordinator.start(second, List.of(first, second), Pet::point, Pet::id, pet -> true, 41).isEmpty());
        assertTrue(coordinator.start(second, List.of(first, second), Pet::point, Pet::id, pet -> true, 141).isPresent());
    }

    @Test void disappearanceAndSafetyCancelBothParticipants() {
        Pet first = new Pet(1, new Point());
        Pet second = new Pet(2, new Point(1, 0));
        InteractionCoordinator<Pet> coordinator = new InteractionCoordinator<>(10, 5);
        coordinator.start(first, List.of(first, second), Pet::point, Pet::id, pet -> true, 0);
        coordinator.advance(1, pet -> pet != second, pet -> false);
        assertEquals(0, coordinator.activeSessionCount());
        coordinator.start(first, List.of(first, second), Pet::point, Pet::id, pet -> true, 10);
        coordinator.advance(11, pet -> true, pet -> pet == first);
        assertFalse(coordinator.isClaimed(first));
        assertFalse(coordinator.isClaimed(second));
    }

    @Test void stressCountsNeverDuplicateClaimsAndCooldownHistoryIsBounded() {
        for (int count : List.of(1, 5, 10, 25)) {
            List<Pet> pets = java.util.stream.IntStream.range(0, count)
                    .mapToObj(i -> new Pet(i, new Point(i, 0))).toList();
            InteractionCoordinator<Pet> coordinator = new InteractionCoordinator<>(1, 1000);
            for (Pet pet : pets) coordinator.start(pet, pets, Pet::point, Pet::id, ignored -> true, 0);
            assertTrue(coordinator.activeSessionCount() <= count / 2);
            assertEquals(coordinator.activeSessionCount() * 2,
                    pets.stream().filter(coordinator::isClaimed).count());
        }
    }
}
