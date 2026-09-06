package com.group_finity.mascot.terrain;

import com.group_finity.mascot.platform.window.WindowSnapshot;

import java.util.Collection;
import java.util.Objects;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Supplier;

/** Non-blocking, bounded-rate producer of immutable window terrain snapshots. */
public final class WindowTerrainRefresh {
    public record Snapshot(long generation, WindowTerrain terrain) { }

    private final Supplier<? extends Collection<WindowSnapshot>> discovery;
    private final Executor executor;
    private final long intervalMillis;
    private final AtomicBoolean inFlight = new AtomicBoolean();
    private volatile long nextRefreshMillis;
    private volatile Snapshot current = new Snapshot(0, WindowTerrain.EMPTY);

    public WindowTerrainRefresh(Supplier<? extends Collection<WindowSnapshot>> discovery,
                                Executor executor, long intervalMillis) {
        this.discovery = Objects.requireNonNull(discovery, "discovery");
        this.executor = Objects.requireNonNull(executor, "executor");
        if (intervalMillis < 1) throw new IllegalArgumentException("intervalMillis must be positive");
        this.intervalMillis = intervalMillis;
    }

    /** Schedules at most one refresh per interval and never waits for native discovery. */
    public void refreshIfDue(long nowMillis) {
        if (nowMillis < nextRefreshMillis || !inFlight.compareAndSet(false, true)) return;
        nextRefreshMillis = nowMillis + intervalMillis;
        executor.execute(() -> {
            try {
                WindowTerrain terrain = WindowTerrain.fromSnapshots(discovery.get());
                current = new Snapshot(current.generation() + 1, terrain);
            } finally {
                inFlight.set(false);
            }
        });
    }

    public Snapshot current() {
        return current;
    }
}
