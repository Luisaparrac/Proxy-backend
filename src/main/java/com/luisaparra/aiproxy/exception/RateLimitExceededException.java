package com.luisaparra.aiproxy.exception;

/**
 * Thrown when a user exceeds the requests per minute allowed by its plan (HTTP 429).
 */
public class RateLimitExceededException extends RuntimeException {

    private final int retryAfterSeconds;

    public RateLimitExceededException(int requestsPerMinute, int retryAfterSeconds) {
        super("Superaste el límite de " + requestsPerMinute
                + " solicitudes por minuto. Intenta de nuevo en " + retryAfterSeconds + " segundos.");
        this.retryAfterSeconds = retryAfterSeconds;
    }

    public int getRetryAfterSeconds() {
        return retryAfterSeconds;
    }
}
