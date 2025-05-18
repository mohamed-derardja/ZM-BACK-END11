package com.zm.zmbackend.exceptions;

/**
 * Exception thrown when a reservation request contains invalid data
 * or violates business rules.
 */
public class InvalidReservationException extends RuntimeException {
    
    public InvalidReservationException(String message) {
        super(message);
    }

}