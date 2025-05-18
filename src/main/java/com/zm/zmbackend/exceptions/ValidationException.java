package com.zm.zmbackend.exceptions;

/**
 * Exception thrown when input data fails validation checks,
 * such as invalid dates, missing required fields, or invalid formats.
 */
public class ValidationException extends RuntimeException {
    
    public ValidationException(String message) {
        super(message);
    }

}