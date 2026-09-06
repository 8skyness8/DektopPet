package com.group_finity.mascot.terrain;

import com.group_finity.mascot.platform.window.Bounds;
import com.group_finity.mascot.platform.window.WindowSnapshot;
import org.junit.jupiter.api.Test;

import java.util.ArrayDeque;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;

class WindowTerrainRefreshTest {
    @Test void refreshIsNonBlockingSingleFlightAndRateLimited() {
        Queue<Runnable> work = new ArrayDeque<>();
        AtomicInteger calls = new AtomicInteger();
        WindowTerrainRefresh refresh = new WindowTerrainRefresh(() -> {
            int x = calls.incrementAndGet() * 10;
            return List.of(snapshot(7, x, 20, 100, 80));
        }, work::add, 250);

        refresh.refreshIfDue(1_000);
        refresh.refreshIfDue(1_001);
        assertEquals(1, work.size());
        assertEquals(0, calls.get());
        work.remove().run();
        assertEquals(1, refresh.current().generation());

        refresh.refreshIfDue(1_249);
        assertEquals(0, work.size());
        refresh.refreshIfDue(1_250);
        assertEquals(1, work.size());
    }

    static WindowSnapshot snapshot(long id, int x, int y, int width, int height) {
        return new WindowSnapshot(id, new Bounds(x, y, width, height), "eligible",
                true, false, false, false, false, false, false, true);
    }
}
