package com.zm.zmbackend.exceptions;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

/**
 * Standard error response object that will be returned to clients
 * when an exception occurs.
 */
@Setter
@Getter
public class ErrorResponse {
    // Getters and setters
    private HttpStatus status;
    private int statusCode;
    private String message;
    private String path;
    
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime timestamp;
    
    public ErrorResponse() {
        this.timestamp = LocalDateTime.now();
    }
    
    public ErrorResponse(HttpStatus status, String message) {
        this();
        this.status = status;
        this.statusCode = status.value();
        this.message = message;
    }
    
    public ErrorResponse(HttpStatus status, String message, String path) {
        this(status, message);
        this.path = path;
    }

}