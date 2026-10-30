package com.luisaparra.aiproxy.exception;

/**
 * Thrown when the X-User-Id header does not match any user (HTTP 404).
 */
public class UserNotFoundException extends RuntimeException {

    public UserNotFoundException(String userId) {
        super("No existe un usuario con id '" + userId + "'.");
    }
}
