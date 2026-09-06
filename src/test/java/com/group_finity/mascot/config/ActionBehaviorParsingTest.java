package com.group_finity.mascot.config;

import com.group_finity.mascot.script.VariableMap;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ActionBehaviorParsingTest {
    private Configuration configuration;

    @BeforeEach
    void loadEnglishSchema() throws Exception {
        configuration = new Configuration();
        configuration.load(EntryTest.entry("<Mascot/>"), "DevPet", true);
    }

    @Test
    void parsesARepresentativeSimpleAction() throws Exception {
        ActionBuilder action = new ActionBuilder(configuration,
                EntryTest.entry("<Action Name=\"Stand\" Type=\"Stay\" Duration=\"120\"/>"), "DevPet");

        assertEquals("Stand", action.getName());
        assertEquals("Action[name=Stand,Stay]", action.toString());
    }

    @Test
    void behaviorUsesExistingDefaultsWhenOptionalAttributesAreAbsent() throws Exception {
        BehaviorBuilder behavior = new BehaviorBuilder(configuration,
                EntryTest.entry("<Behavior Name=\"Stand\" Frequency=\"10\"/>"), List.of());

        assertEquals("Stand", behavior.getName());
        assertEquals(10, behavior.getFrequency());
        assertFalse(behavior.isHidden());
        assertFalse(behavior.isToggleable());
        assertTrue(behavior.isNextAdditive());
        assertTrue(behavior.getNextBehaviorBuilders().isEmpty());
    }

    @Test
    void behaviorConditionIsParsedAndEvaluated() throws Exception {
        BehaviorBuilder behavior = new BehaviorBuilder(configuration,
                EntryTest.entry("<Behavior Name=\"Stand\" Frequency=\"1\" Condition=\"${1 &lt; 2}\"/>"), List.of());

        assertTrue(behavior.isEffective(new VariableMap()));
    }

    @Test
    void behaviorRejectsANonNumericFrequency() throws Exception {
        Entry behavior = EntryTest.entry("<Behavior Name=\"Stand\" Frequency=\"often\"/>");

        assertThrows(NumberFormatException.class,
                () -> new BehaviorBuilder(configuration, behavior, List.of()));
    }
}
