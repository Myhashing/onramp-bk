package com.amanatpay.onramp.exception;

import com.inversoft.error.Errors;
import lombok.Getter;

/**
 * Custom exception class for handling FusionAuth-related errors.
 */
@Getter
public class FusionAuthException extends Exception {
    /**
     * -- GETTER --
     *  Retrieves the FusionAuth errors associated with this exception.
     *
     * @return the FusionAuth errors
     */
    private Errors errors;

    /**
     * Constructs a new FusionAuthException with the specified detail message.
     *
     * @param message the detail message
     */
    public FusionAuthException(String message) {
        super(message);
    }

    /**
     * Constructs a new FusionAuthException with the specified detail message and cause.
     *
     * @param message the detail message
     * @param cause the cause (can be retrieved later by the {@link Throwable#getCause()} method)
     */
    public FusionAuthException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructs a new FusionAuthException with the specified detail message and FusionAuth errors.
     *
     * @param message the detail message
     * @param errors the FusionAuth errors associated with this exception
     */
    public FusionAuthException(String message, Errors errors) {
        super(message);
        this.errors = errors;
    }

}