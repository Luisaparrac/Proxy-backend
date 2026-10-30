package com.luisaparra.aiproxy.exception;

/**
 * Thrown when a plan upgrade is not allowed (HTTP 400).
 */
public class InvalidUpgradeException extends RuntimeException {

    public InvalidUpgradeException(String message) {
        super(message);
    }
}
