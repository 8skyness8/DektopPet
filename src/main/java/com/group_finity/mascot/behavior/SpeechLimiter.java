package com.group_finity.mascot.behavior;

import java.util.HashMap;
import java.util.Map;
import java.util.function.LongSupplier;

/** Small process-wide gate preventing overlapping mascot speech. */
public final class SpeechLimiter {
    private final LongSupplier clock;
    private final Map<Integer, Long> mascotReady = new HashMap<>();
    private long globalReady;

    public SpeechLimiter(LongSupplier clock) { this.clock = clock; }

    public synchronized boolean acquire(int mascotId, long mascotCooldownMillis, long globalCooldownMillis) {
        long now = clock.getAsLong();
        if (now < globalReady || now < mascotReady.getOrDefault(mascotId, 0L)) return false;
        globalReady = now + Math.max(0, globalCooldownMillis);
        mascotReady.put(mascotId, now + Math.max(0, mascotCooldownMillis));
        return true;
    }
}
