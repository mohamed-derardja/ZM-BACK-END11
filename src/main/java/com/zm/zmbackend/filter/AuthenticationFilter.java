package com.zm.zmbackend.filter;

import com.zm.zmbackend.services.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.constraints.NotNull;
import org.springframework.http.HttpStatus;
import org.springframework.web.servlet.HandlerInterceptor;


public class AuthenticationFilter implements HandlerInterceptor {

    private final UserService userService;

    public AuthenticationFilter(UserService userService) {
        this.userService = userService;
    }

    @Override
    public boolean preHandle( HttpServletRequest request,@NotNull
                              HttpServletResponse response,
                              Object handler) throws Exception {
        // Get the token from the request header
        String authToken = request.getHeader("Authorization");
        if (authToken == null || authToken.isEmpty()) {
            // Check if X-User-ID header is present (for backward compatibility)
            String userIdHeader = request.getHeader("X-User-ID");
            if (userIdHeader == null || userIdHeader.isEmpty()) {
                response.setStatus(HttpStatus.UNAUTHORIZED.value());
                response.getWriter().write("Authentication required");
                return false;
            }
            
            // Validate the user ID
            try {
                Long userId = Long.parseLong(userIdHeader);
                if (!userService.isAuthenticated(userId)) {
                    response.setStatus(HttpStatus.UNAUTHORIZED.value());
                    response.getWriter().write("Invalid user ID");
                    return false;
                }
                
                // Set the user ID as a request attribute for controllers to use
                request.setAttribute("currentUserId", userId);
                return true;
            } catch (NumberFormatException e) {
                response.setStatus(HttpStatus.BAD_REQUEST.value());
                response.getWriter().write("Invalid user ID format");
                return false;
            }
        }
        
        // Validate the token
        if (authToken.startsWith("Bearer ")) {
            authToken = authToken.substring(7);
        }
        
        Long userId = userService.validateToken(authToken);
        if (userId == null) {
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            response.getWriter().write("Invalid or expired token");
            return false;
        }
        
        // Set the user ID as a request attribute for controllers to use
        request.setAttribute("currentUserId", userId);
        return true;
    }
}