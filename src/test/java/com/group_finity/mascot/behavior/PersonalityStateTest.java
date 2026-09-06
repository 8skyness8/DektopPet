package com.group_finity.mascot.behavior;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PersonalityStateTest {
    @Test void configuredStyleIncrementAccumulatesAndCapsDeterministically() {
        PersonalityState state = new PersonalityState();
        assertEquals(2, state.increment("curiosity", 2, 3));
        assertEquals(3, state.increment("curiosity", 2, 3));
        assertEquals(3, state.get("curiosity"));
        assertEquals(0, state.increment("curiosity", -9, 3));
    }
}
