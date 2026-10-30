package com.luisaparra.aiproxy.dto;

import com.luisaparra.aiproxy.model.Plan;

/**
 * Result of a simulated plan upgrade.
 */
public record UpgradeResponse(
        String userId,
        Plan previousPlan,
        Plan newPlan,
        String transactionId,
        double amountChargedUsd,
        String message
) {
}
