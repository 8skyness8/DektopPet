package com.group_finity.mascot.behavior;

import org.junit.jupiter.api.Test;
import java.util.List;
import java.util.Random;
import static org.junit.jupiter.api.Assertions.*;

class NaturalBehaviorSelectorTest {
    private final NaturalBehaviorSelector selector = new NaturalBehaviorSelector();

    @Test void suppressesImmediateRepeatAndAllowsItAfterCooldown() {
        NaturalBehaviorState state = new NaturalBehaviorState();
        var rest = candidate("rest", 100, 3, 0, 0);
        var walk = candidate("walk", 1, 0, 0, 0);
        assertEquals("rest", selector.select(List.of(rest, walk), state, false, false, new Random(1)));
        assertEquals("walk", selector.select(List.of(rest, walk), state, false, false, new Random(1)));
        state.advance(3);
        assertEquals("rest", selector.select(List.of(rest, walk), state, false, false, new Random(1)));
    }

    @Test void allSuppressedFallsBackWithoutStarvation() {
        NaturalBehaviorState state = new NaturalBehaviorState();
        var only = candidate("only", 1, 50, 0, 0);
        assertEquals("only", selector.select(List.of(only), state, false, false, new Random(2)));
        assertEquals("only", selector.select(List.of(only), state, false, false, new Random(2)));
    }

    @Test void needsAndContextAdjustCompetingBaseFrequenciesDeterministically() {
        NaturalBehaviorState state = new NaturalBehaviorState();
        state.getNeeds().set("boredom", 100, 100);
        var explore = candidate("explore", 100, 0, 1, 0);
        var attention = candidate("attention", 1, 0, 0, 200);
        assertEquals("explore", selector.select(List.of(explore, attention), state, false, false, new Random(0)));
        NaturalBehaviorState other = new NaturalBehaviorState();
        assertEquals("attention", selector.select(List.of(explore, attention), other, true, false, new Random(0)));
    }

    @Test void needsAdvanceGraduallyRemainBoundedAndInteractionChangesAffection() {
        NaturalBehaviorState state = new NaturalBehaviorState();
        state.advance(250);
        assertEquals(49, state.getNeeds().get("energy"));
        assertEquals(51, state.getNeeds().get("boredom"));
        state.interaction(80);
        assertEquals(100, state.getNeeds().get("affection"));
        state.advance(50_000);
        assertTrue(state.getNeeds().get("energy") >= 0);
        assertTrue(state.getNeeds().get("boredom") <= 100);
    }

    @Test void optionalRelationshipMetadataAdjustsUtility() {
        NaturalBehaviorState state = new NaturalBehaviorState();
        state.setRelationship(100);
        var bonded = new NaturalBehaviorSelector.Candidate<>("bonded", "bonded", 10, 0,
                0, 0, 0, 0, 0, 0, 0, 5);
        var neutral = candidate("neutral", 10, 0, 0, 0);
        assertEquals("bonded", selector.select(List.of(bonded, neutral), state, false, false, new Random(0)));
    }

    private NaturalBehaviorSelector.Candidate<String> candidate(String name, int frequency, int cooldown,
                                                                  double boredom, double cursor) {
        return new NaturalBehaviorSelector.Candidate<>(name, name, frequency, cooldown,
                0, boredom, 0, 0, 0, cursor, 0, 0);
    }
}
