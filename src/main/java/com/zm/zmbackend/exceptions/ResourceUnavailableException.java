package com.zm.zmbackend.exceptions;

import lombok.Getter;

/**
 * Exception thrown when a requested resource (car, driver, etc.) is not available
 * for the requested time period.
 */
@Getter
public class ResourceUnavailableException extends RuntimeException {
    
    private final String resourceType;
    private final Long resourceId;
    
    public ResourceUnavailableException(String resourceType, Long resourceId) {
        super(String.format("%s with id %d is not available for the requested time period", resourceType, resourceId));
        this.resourceType = resourceType;
        this.resourceId = resourceId;
    }

}