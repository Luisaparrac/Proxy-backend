package com.luisaparra.aiproxy.model;

/**
 * A platform user with its current plan and monthly token consumption.
 * Methods that change state are synchronized because several requests
 * from the same user can arrive at the same time.
 */
public class UserAccount {

    private final String id;
    private final String name;
    private Plan plan;
    private long tokensUsedThisMonth;

    public UserAccount(String id, String name, Plan plan, long tokensUsedThisMonth) {
        this.id = id;
        this.name = name;
        this.plan = plan;
        this.tokensUsedThisMonth = tokensUsedThisMonth;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public synchronized Plan getPlan() {
        return plan;
    }

    public synchronized long getTokensUsedThisMonth() {
        return tokensUsedThisMonth;
    }

    /**
     * Remaining tokens for the month. Returns {@link Long#MAX_VALUE} when the plan is unlimited.
     */
    public synchronized long getRemainingTokens() {
        if (plan.hasUnlimitedTokens()) {
            return Long.MAX_VALUE;
        }
        return Math.max(0, plan.getMonthlyTokenLimit() - tokensUsedThisMonth);
    }

    public synchronized void consumeTokens(long tokens) {
        this.tokensUsedThisMonth += tokens;
    }

    public synchronized void changePlan(Plan newPlan) {
        this.plan = newPlan;
    }

    public synchronized void resetMonthlyUsage() {
        this.tokensUsedThisMonth = 0;
    }
}
