package com.group_finity.mascot.interaction;

import java.awt.Point;
import java.util.Collection;
import java.util.Comparator;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.ToIntFunction;

/** Pure deterministic partner selection for bounded multi-pet interactions. */
public final class InteractionCoordinator {
    private InteractionCoordinator() { }

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
}
