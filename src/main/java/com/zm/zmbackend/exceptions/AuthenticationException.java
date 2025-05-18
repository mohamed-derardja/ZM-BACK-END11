package com.zm.zmbackend.exceptions;

/**
 * Exception thrown when there are issues with user authentication,
 * such as missing authentication, invalid credentials, or required verification.
 */
public class AuthenticationException extends RuntimeException {
    
    public AuthenticationException(String message) {
        super(message);
    }

}