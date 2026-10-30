package com.luisaparra.aiproxy.dto;

import java.time.Instant;

/**
 * Result returned by the AI generation service.
 */
public record GenerationResponse(
        String text,
        String model,
        int promptTokens,
        int completionTokens,
        int totalTokens,
        long latencyMs,
        Instant generatedAt
) {
}
