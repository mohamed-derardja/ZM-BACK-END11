package com.zm.zmbackend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LoginResponse {
    private Long userId;
    private String token;
    private boolean emailVerified;
    // Phone verification removed as per requirements
}
