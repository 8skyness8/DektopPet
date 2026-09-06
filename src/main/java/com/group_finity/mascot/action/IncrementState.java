package com.group_finity.mascot.action;

import com.group_finity.mascot.script.VariableException;
import com.group_finity.mascot.script.VariableMap;

import java.util.ResourceBundle;

/** One-tick generic action that increments a configured mascot personality value. */
public final class IncrementState extends InstantAction {
    public IncrementState(ResourceBundle schema, VariableMap context) {
        super(schema, context);
    }

    @Override protected void apply() throws VariableException {
        String key = eval("StateKey", String.class, "curiosity");
        int amount = eval("StateAmount", Number.class, 1).intValue();
        int maximum = eval("StateMaximum", Number.class, 10).intValue();
        getMascot().getPersonalityState().increment(key, amount, maximum);
    }
}
