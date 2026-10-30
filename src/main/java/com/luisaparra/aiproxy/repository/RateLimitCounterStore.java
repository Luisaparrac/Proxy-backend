package com.luisaparra.aiproxy.repository;

import org.springframework.stereotype.Component;

import java.time.Clock;
import java.time.LocalTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Keeps how many requests each user has made in the current minute.
 * The window is the calendar minute: counters are cleared at second 0
 * by {@code UsageResetScheduler}.
 */
@Component
public class RateLimitCounterStore {

    private static final int WINDOW_SECONDS = 60;

    private final Map<String, AtomicInteger> counters = new ConcurrentHashMap<>();
    private final Clock clock;

    public RateLimitCounterStore(Clock clock) {
        this.clock = clock;
    }

    public int increment(String userId) {
        return counters.computeIfAbsent(userId, id -> new AtomicInteger()).incrementAndGet();
    }

    public void decrement(String userId) {
        AtomicInteger counter = counters.get(userId);
        if (counter != null) {
            counter.updateAndGet(value -> Math.max(0, value - 1));
        }
    }

    public int getCount(String userId) {
        AtomicInteger counter = counters.get(userId);
        return counter == null ? 0 : counter.get();
    }

    public int secondsUntilReset() {
        int currentSecond = LocalTime.now(clock).getSecond();
        return Math.max(1, WINDOW_SECONDS - currentSecond);
    }

    public void resetAll() {
        counters.clear();
    }
}
