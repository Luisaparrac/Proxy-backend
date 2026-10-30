package com.luisaparra.aiproxy.model;

import java.time.LocalDate;

/**
 * Tokens and successful requests consumed by a user on a given day.
 */
public class DailyUsage {

    private final LocalDate date;
    private long tokens;
    private int requests;

    public DailyUsage(LocalDate date) {
        this(date, 0, 0);
    }

    public DailyUsage(LocalDate date, long tokens, int requests) {
        this.date = date;
        this.tokens = tokens;
        this.requests = requests;
    }

    public synchronized void add(long consumedTokens) {
        this.tokens += consumedTokens;
        this.requests++;
    }

    public LocalDate getDate() {
        return date;
    }

    public synchronized long getTokens() {
        return tokens;
    }

    public synchronized int getRequests() {
        return requests;
    }
}
