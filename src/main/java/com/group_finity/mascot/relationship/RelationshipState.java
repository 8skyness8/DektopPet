package com.group_finity.mascot.relationship;

import java.util.EnumMap;
import java.util.Map;

/** A small, bounded, character-level relationship value with per-gesture anti-farming. */
public final class RelationshipState {
    public static final int MIN_BOND = 0;
    public static final int MAX_BOND = 100;
    public static final int DEFAULT_BOND = 50;

    private final Map<InteractionEvent, Long> lastAccepted = new EnumMap<>(InteractionEvent.class);
    private int bond;

    public RelationshipState() { this(DEFAULT_BOND); }

    public RelationshipState(int bond) { this.bond = clamp(bond); }

    public synchronized boolean apply(InteractionEvent event, int delta, long cooldownMillis, long nowMillis) {
        Long last = lastAccepted.get(event);
        if (last != null && nowMillis >= last && nowMillis - last < Math.max(0, cooldownMillis)) return false;
        bond = clamp((long) bond + delta);
        lastAccepted.put(event, nowMillis);
        return true;
    }

    public synchronized int getBond() { return bond; }

    synchronized void restoreAccepted(InteractionEvent event, long timestamp) {
        if (timestamp >= 0) lastAccepted.put(event, timestamp);
    }

    synchronized Map<InteractionEvent, Long> acceptedSnapshot() { return Map.copyOf(lastAccepted); }

    private static int clamp(long value) { return (int) Math.max(MIN_BOND, Math.min(MAX_BOND, value)); }
}
