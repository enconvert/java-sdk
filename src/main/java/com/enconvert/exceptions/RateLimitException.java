package com.enconvert.exceptions;

/** Thrown on HTTP 429 — too many requests. */
public class RateLimitException extends ApiException {

    private static final long serialVersionUID = 1L;

    public RateLimitException() {
        this("Rate limit exceeded");
    }

    public RateLimitException(String message) {
        super(429, message);
    }
}
