package com.group_finity.mascot.action;

import com.group_finity.mascot.Main;
import com.group_finity.mascot.script.VariableException;
import com.group_finity.mascot.script.VariableMap;

import java.util.ResourceBundle;

/** Displays a short, configuration-provided message inside the mascot window. */
public final class Say extends InstantAction {
    public Say(ResourceBundle schema, VariableMap context) {
        super(schema, context);
    }

    @Override
    protected void apply() throws VariableException {
        if (Main.getInstance().getSettings().presentationBubbles) {
            String text = eval(getSchema().getString("Text"), String.class, "").trim();
            if (!text.isEmpty()) {
                getMascot().showPresentationBubble(text, 75);
            }
        }
    }
}
