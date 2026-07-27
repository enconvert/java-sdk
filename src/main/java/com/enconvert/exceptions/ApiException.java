package com.enconvert.exceptions;

/** An HTTP error response from the Enconvert API (status &gt;= 400). */
public class ApiException extends EnconvertException {

    private static final long serialVersionUID = 1L;

    private final int statusCode;

    public ApiException(int statusCode, String message) {
        super("[" + statusCode + "] " + message);
        this.statusCode = statusCode;
    }

    /** The HTTP status code returned by the API. */
    public int getStatusCode() {
        return statusCode;
    }
}
