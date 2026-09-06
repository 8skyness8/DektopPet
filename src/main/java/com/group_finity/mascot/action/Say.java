package com.group_finity.mascot.action;

import com.group_finity.mascot.Main;
import com.group_finity.mascot.script.VariableException;
import com.group_finity.mascot.script.VariableMap;
import com.group_finity.mascot.behavior.SpeechLimiter;

import java.util.ResourceBundle;

/** Displays a short, configuration-provided message inside the mascot window. */
public final class Say extends InstantAction {
    private static final SpeechLimiter LIMITER = new SpeechLimiter(System::currentTimeMillis);
    public Say(ResourceBundle schema, VariableMap context) {
        super(schema, context);
    }

    @Override
    protected void apply() throws VariableException {
        if (Main.getInstance().getSettings().presentationBubbles) {
            String pool = eval(getSchema().getString("Phrases"), String.class,
                    eval(getSchema().getString("Text"), String.class, "")).trim();
            String[] phrases = java.util.Arrays.stream(pool.split("\\|"))
                    .map(String::trim).filter(s -> !s.isEmpty()).toArray(String[]::new);
            if (phrases.length > 0 && !getMascot().hasPresentationBubble()
                    && LIMITER.acquire(getMascot().getId(), 12_000, 2_500)) {
                String text = phrases[java.util.concurrent.ThreadLocalRandom.current().nextInt(phrases.length)];
                getMascot().showPresentationBubble(text, 75);
            }
        }
    }
}
