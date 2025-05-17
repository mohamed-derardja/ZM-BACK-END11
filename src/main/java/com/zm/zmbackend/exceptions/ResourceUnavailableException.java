package com.zm.zmbackend.exceptions;

/**
 * Exception thrown when a requested resource (car, driver, etc.) is not available
 * for the requested time period.
 */
public class ResourceUnavailableException extends RuntimeException {
    
    private String resourceType;
    private Long resourceId;
    
    public ResourceUnavailableException(String resourceType, Long resourceId) {
        super(String.format("%s with id %d is not available for the requested time period", resourceType, resourceId));
        this.resourceType = resourceType;
        this.resourceId = resourceId;
    }
    
    public ResourceUnavailableException(String message) {
        super(message);
    }
    
    public String getResourceType() {
        return resourceType;
    }
    
    public Long getResourceId() {
        return resourceId;
    }
}