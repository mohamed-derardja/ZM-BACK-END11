package com.zm.zmbackend.exceptions;

/**
 * Exception thrown when a user exceeds the allowed rate of requests
 * or operations within a specified time period.
 */
public class RateLimitExceededException extends RuntimeException {
    
    public RateLimitExceededException(String message) {
        super(message);
    }

}