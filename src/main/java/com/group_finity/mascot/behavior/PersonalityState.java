package com.group_finity.mascot.behavior;

import java.util.LinkedHashMap;
import java.util.Map;

/** Small generic integer state store used by configuration-driven personality actions. */
public final class PersonalityState {
    private final Map<String, Integer> values = new LinkedHashMap<>();

    public synchronized int get(String name) {
        return values.getOrDefault(name, 0);
    }

    public synchronized int increment(String name, int amount, int maximum) {
        int next = Math.max(0, Math.min(maximum, Math.addExact(get(name), amount)));
        values.put(name, next);
        return next;
    }

    public synchronized int set(String name, int value, int maximum) {
        int bounded = Math.max(0, Math.min(maximum, value));
        values.put(name, bounded);
        return bounded;
    }
}
