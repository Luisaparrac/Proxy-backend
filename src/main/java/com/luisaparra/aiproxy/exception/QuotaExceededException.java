package com.luisaparra.aiproxy.exception;

/**
 * Thrown when a user has no monthly tokens left for the request (HTTP 402).
 */
public class QuotaExceededException extends RuntimeException {

    public QuotaExceededException(long remainingTokens) {
        super(remainingTokens <= 0
                ? "Agotaste tu cuota mensual de tokens. Mejora tu plan para seguir usando el servicio."
                : "Te quedan solo " + remainingTokens
                + " tokens, no alcanzan para este prompt. Mejora tu plan para continuar.");
    }
}
