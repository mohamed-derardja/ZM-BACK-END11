package com.zm.zmbackend.exceptions;

/**
 * Exception thrown when a user attempts to access a resource or perform an action
 * they are not authorized to access or perform.
 */
public class AuthorizationException extends RuntimeException {
    
    public AuthorizationException(String message) {
        super(message);
    }
    

}