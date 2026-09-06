package com.group_finity.mascot.behavior;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.LinkedHashMap;
import java.util.Map;

/** Bounded, per-mascot memory and reusable needs for natural behavior decisions. */
public final class NaturalBehaviorState {
    public static final int MIN_NEED = 0;
    public static final int MAX_NEED = 100;
    private static final int HISTORY_LIMIT = 8;

    private final Deque<String> history = new ArrayDeque<>(HISTORY_LIMIT);
    private final Map<String, Long> cooldowns = new LinkedHashMap<>();
    private final PersonalityState needs = new PersonalityState();
    private long tick;
    private long lastInteractionTick;
    private int relationship = 50;

    public NaturalBehaviorState() {
        for (String need : new String[]{"energy", "boredom", "curiosity", "affection", "social"}) {
            needs.set(need, 50, MAX_NEED);
        }
    }

    public synchronized void advance(long ticks) {
        if (ticks <= 0) return;
        tick += ticks;
        // Slow, bounded changes; event-specific policy remains configuration-driven.
        if (tick % 250 < ticks) {
            needs.increment("energy", -1, MAX_NEED);
            needs.increment("boredom", 1, MAX_NEED);
            needs.increment("curiosity", 1, MAX_NEED);
            needs.increment("social", 1, MAX_NEED);
        }
    }

    public synchronized void interaction(int affectionChange) {
        lastInteractionTick = tick;
        needs.increment("affection", affectionChange, MAX_NEED);
        needs.increment("boredom", -5, MAX_NEED);
    }

    public synchronized void selected(String name, long cooldownTicks) {
        cooldowns.entrySet().removeIf(entry -> entry.getValue() <= tick);
        while (cooldowns.size() >= 64) cooldowns.remove(cooldowns.keySet().iterator().next());
        if (history.size() == HISTORY_LIMIT) history.removeFirst();
        history.addLast(name);
        if (cooldownTicks > 0) cooldowns.put(name, tick + cooldownTicks);
    }

    public synchronized boolean isSuppressed(String name) {
        return (!history.isEmpty() && history.getLast().equals(name)) || cooldowns.getOrDefault(name, 0L) > tick;
    }

    public synchronized int recentCount(String name) {
        return (int) history.stream().filter(name::equals).count();
    }

    public synchronized long getTick() { return tick; }
    public synchronized long getTicksSinceInteraction() { return tick - lastInteractionTick; }
    public PersonalityState getNeeds() { return needs; }
    public synchronized int getRelationship() { return relationship; }
    public synchronized void setRelationship(int relationship) {
        this.relationship = Math.max(MIN_NEED, Math.min(MAX_NEED, relationship));
    }
}
