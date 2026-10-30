package com.luisaparra.aiproxy.service;

import org.springframework.stereotype.Component;

/**
 * Approximates the number of tokens in a text.
 * Uses the common rule of thumb of about 4 characters per token.
 * The frontend applies the same rule for its estimator.
 */
@Component
public class TokenEstimator {

    private static final double CHARACTERS_PER_TOKEN = 4.0;

    public int estimate(String text) {
        if (text == null || text.isBlank()) {
            return 0;
        }
        return (int) Math.ceil(text.trim().length() / CHARACTERS_PER_TOKEN);
    }
}
