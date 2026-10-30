package com.luisaparra.aiproxy.model;

/**
 * Subscription plans with their request-per-minute and monthly token limits.
 * A value of {@link #UNLIMITED} means the plan has no limit.
 */
public enum Plan {

    FREE("Free", 10, 50_000L, 0.0),
    PRO("Pro", 60, 500_000L, 19.99),
    ENTERPRISE("Enterprise", Plan.UNLIMITED, Plan.UNLIMITED, 99.99);

    public static final int UNLIMITED = -1;

    private final String displayName;
    private final int requestsPerMinute;
    private final long monthlyTokenLimit;
    private final double monthlyPriceUsd;

    Plan(String displayName, int requestsPerMinute, long monthlyTokenLimit, double monthlyPriceUsd) {
        this.displayName = displayName;
        this.requestsPerMinute = requestsPerMinute;
        this.monthlyTokenLimit = monthlyTokenLimit;
        this.monthlyPriceUsd = monthlyPriceUsd;
    }

    public String getDisplayName() {
        return displayName;
    }

    public int getRequestsPerMinute() {
        return requestsPerMinute;
    }

    public long getMonthlyTokenLimit() {
        return monthlyTokenLimit;
    }

    public double getMonthlyPriceUsd() {
        return monthlyPriceUsd;
    }

    public boolean hasUnlimitedRequests() {
        return requestsPerMinute == UNLIMITED;
    }

    public boolean hasUnlimitedTokens() {
        return monthlyTokenLimit == UNLIMITED;
    }
}
