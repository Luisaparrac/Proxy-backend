package com.luisaparra.aiproxy.dto;

/**
 * Internal request that travels through the proxy chain.
 *
 * @param userId user that sends the prompt
 * @param prompt text to generate from
 */
public record GenerationRequest(String userId, String prompt) {
}
