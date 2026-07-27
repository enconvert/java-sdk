package com.enconvert.exceptions;

/** Thrown on HTTP 401/403 — the API key is missing, invalid, or lacks access. */
public class AuthenticationException extends ApiException {

    private static final long serialVersionUID = 1L;

    public AuthenticationException() {
        this("Invalid or missing API key");
    }

    public AuthenticationException(String message) {
        super(401, message);
    }
}
