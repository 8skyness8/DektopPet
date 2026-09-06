package com.group_finity.mascot.interaction;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.ToIntFunction;

/** Bounded deterministic ownership and lifecycle for short two-mascot social encounters. */
public final class InteractionCoordinator<T> {
    public enum Phase { NOTICE, APPROACH, GREET, TOGETHER }
    public record Session<T>(T first, T second, Phase phase, long phaseStarted) {
        boolean includes(T participant) { return first == participant || second == participant; }
    }

    private static final int MAX_COOLDOWNS = 256;
    private final long phaseDuration;
    private final long cooldownDuration;
    private final Map<T, Session<T>> claims = new HashMap<>();
    private final LinkedHashMap<Pair<T>, Long> cooldowns = new LinkedHashMap<>();

    public InteractionCoordinator(long phaseDuration, long cooldownDuration) {
        this.phaseDuration = Math.max(1, phaseDuration);
        this.cooldownDuration = Math.max(0, cooldownDuration);
    }

    public synchronized Optional<Session<T>> start(T requester, Collection<T> candidates,
            Function<T, Point> position, ToIntFunction<T> stableId, Predicate<T> eligible, long now) {
        if (!eligible.test(requester) || claims.containsKey(requester)) return Optional.empty();
        Optional<T> partner = nearest(requester, candidates.stream()
                .filter(eligible).filter(candidate -> !claims.containsKey(candidate))
                .filter(candidate -> !cooling(requester, candidate, stableId, now)).toList(), position, stableId);
        if (partner.isEmpty()) return Optional.empty();
        Session<T> session = new Session<>(requester, partner.get(), Phase.NOTICE, now);
        claims.put(requester, session);
        claims.put(partner.get(), session);
        return Optional.of(session);
    }

    public synchronized List<Session<T>> advance(long now, Predicate<T> available, Predicate<T> safetyRequired) {
        List<Session<T>> completed = new ArrayList<>();
        for (Session<T> session : uniqueSessions()) {
            if (!available.test(session.first) || !available.test(session.second)
                    || safetyRequired.test(session.first) || safetyRequired.test(session.second)) {
                release(session, now);
            } else if (now - session.phaseStarted >= phaseDuration) {
                if (session.phase == Phase.TOGETHER) {
                    release(session, now);
                    completed.add(session);
                } else {
                    Phase next = Phase.values()[session.phase.ordinal() + 1];
                    Session<T> advanced = new Session<>(session.first, session.second, next, now);
                    claims.put(session.first, advanced);
                    claims.put(session.second, advanced);
                }
            }
        }
        cleanup(now);
        return completed;
    }

    public synchronized void cancel(T participant, long now) {
        Session<T> session = claims.get(participant);
        if (session != null) release(session, now);
    }

    public synchronized boolean isClaimed(T participant) { return claims.containsKey(participant); }
    public synchronized int activeSessionCount() { return claims.size() / 2; }
    public synchronized int cooldownCount() { return cooldowns.size(); }

    private List<Session<T>> uniqueSessions() {
        return claims.values().stream().distinct().toList();
    }

    private void release(Session<T> session, long now) {
        claims.remove(session.first);
        claims.remove(session.second);
        cooldowns.put(Pair.of(session.first, session.second), now + cooldownDuration);
        cooldowns.put(Pair.of(session.second, session.first), now + cooldownDuration);
        while (cooldowns.size() > MAX_COOLDOWNS) cooldowns.remove(cooldowns.keySet().iterator().next());
    }

    private boolean cooling(T first, T second, ToIntFunction<T> ids, long now) {
        return cooldowns.getOrDefault(Pair.of(first, second), 0L) > now;
    }

    private void cleanup(long now) { cooldowns.entrySet().removeIf(entry -> entry.getValue() <= now); }

    public static <T> Optional<T> nearest(T requester, Collection<T> candidates,
            Function<T, Point> position, ToIntFunction<T> stableId) {
        Point origin = position.apply(requester);
        return candidates.stream().filter(candidate -> candidate != requester)
                .min(Comparator.comparingLong((T candidate) -> distanceSquared(origin, position.apply(candidate)))
                        .thenComparingInt(stableId));
    }

    private static long distanceSquared(Point first, Point second) {
        long dx = (long) first.x - second.x;
        long dy = (long) first.y - second.y;
        return dx * dx + dy * dy;
    }

    private record Pair<T>(T first, T second) {
        static <T> Pair<T> of(T first, T second) { return new Pair<>(first, second); }
    }
}
