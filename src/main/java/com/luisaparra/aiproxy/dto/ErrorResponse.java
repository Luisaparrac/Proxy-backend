package com.luisaparra.aiproxy.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.Instant;

/**
 * Standard error body. {@code retryAfterSeconds} is only sent on 429 responses.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record ErrorResponse(
        Instant timestamp,
        int status,
        String error,
        String message,
        Integer retryAfterSeconds
) {

    public static ErrorResponse of(int status, String error, String message) {
        return new ErrorResponse(Instant.now(), status, error, message, null);
    }

    public static ErrorResponse withRetryAfter(int status, String error, String message, int retryAfterSeconds) {
        return new ErrorResponse(Instant.now(), status, error, message, retryAfterSeconds);
    }
}
