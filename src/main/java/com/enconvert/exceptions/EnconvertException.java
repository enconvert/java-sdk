package com.enconvert.exceptions;

/**
 * Base class for every exception raised by the Enconvert SDK.
 *
 * <p>Unchecked (extends {@link RuntimeException}) so call sites can opt into
 * catching it without every SDK method declaring a {@code throws} clause.
 */
public class EnconvertException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public EnconvertException(String message) {
        super(message);
    }

    public EnconvertException(String message, Throwable cause) {
        super(message, cause);
    }
}
