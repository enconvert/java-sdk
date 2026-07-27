package com.enconvert.exceptions;

/**
 * Thrown on HTTP 402 — a plan feature is not enabled or the monthly quota is
 * exhausted. Common on V2 endpoints, which are all plan-gated.
 */
public class QuotaException extends ApiException {

    private static final long serialVersionUID = 1L;

    public QuotaException() {
        this("Plan feature not enabled or quota exhausted");
    }

    public QuotaException(String message) {
        super(402, message);
    }
}
