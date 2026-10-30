package com.luisaparra.aiproxy.dto;

import com.luisaparra.aiproxy.model.Plan;

import java.time.LocalDate;

/**
 * Current quota and rate limit state of a user.
 * Limit fields are {@code null} when the plan is unlimited.
 */
public record QuotaStatusResponse(
        String userId,
        String userName,
        Plan plan,
        long tokensUsed,
        Long tokensRemaining,
        Long monthlyTokenLimit,
        boolean unlimitedTokens,
        LocalDate resetDate,
        int requestsUsedThisMinute,
        Integer requestsPerMinuteLimit,
        boolean unlimitedRequests,
        int secondsUntilRateLimitReset
) {
}
