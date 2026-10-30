package com.luisaparra.aiproxy.dto;

import com.luisaparra.aiproxy.model.Plan;

/**
 * Demo user shown in the frontend user selector.
 */
public record UserSummaryResponse(String id, String name, Plan plan) {
}
