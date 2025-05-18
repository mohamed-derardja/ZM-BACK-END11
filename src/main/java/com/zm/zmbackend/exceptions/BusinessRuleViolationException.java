package com.zm.zmbackend.exceptions;

/**
 * Exception thrown when an operation violates a business rule,
 * such as attempting to cancel a reservation that has already started.
 */
public class BusinessRuleViolationException extends RuntimeException {
    
    public BusinessRuleViolationException(String message) {
        super(message);
    }

}