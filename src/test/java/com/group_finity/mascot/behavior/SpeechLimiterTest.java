package com.group_finity.mascot.behavior;

import org.junit.jupiter.api.Test;
import java.util.concurrent.atomic.AtomicLong;
import static org.junit.jupiter.api.Assertions.*;

class SpeechLimiterTest {
    @Test void enforcesGlobalAndPerMascotCooldowns() {
        AtomicLong time = new AtomicLong(100);
        SpeechLimiter limiter = new SpeechLimiter(time::get);
        assertTrue(limiter.acquire(1, 1000, 200));
        assertFalse(limiter.acquire(2, 1000, 200));
        time.addAndGet(200);
        assertTrue(limiter.acquire(2, 1000, 200));
        time.addAndGet(200);
        assertFalse(limiter.acquire(1, 1000, 200));
        time.addAndGet(800);
        assertTrue(limiter.acquire(1, 1000, 200));
    }

    @Test void trackedMascotCooldownsStayBounded() {
        AtomicLong time = new AtomicLong(100);
        SpeechLimiter limiter = new SpeechLimiter(time::get);
        for (int id = 0; id < 500; id++) {
            assertTrue(limiter.acquire(id, 10_000, 0));
        }
        assertTrue(limiter.trackedMascots() <= 128);
    }
}
