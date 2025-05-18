package com.zm.zmbackend.filter;

import com.zm.zmbackend.services.UserService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final UserService userService;

    public JwtAuthenticationFilter(UserService userService) {
        this.userService = userService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        
        // Get the token from the request header
        String authHeader = request.getHeader("Authorization");
        String token = null;
        Long userId = null;
        
        // Check if the Authorization header is present and has the Bearer prefix
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            token = authHeader.substring(7);
            userId = userService.validateToken(token);
        }
        
        // If the token is valid and there's no authentication in the context
        if (userId != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            // Create an authentication token with the user ID as the principal
            UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                userId, null, Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER")));
            
            // Set the authentication details
            authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            
            // Set the authentication in the context
            SecurityContextHolder.getContext().setAuthentication(authToken);
            
            // Set the user ID as a request attribute for controllers to use
            request.setAttribute("currentUserId", userId);
        }
        
        // Continue the filter chain
        filterChain.doFilter(request, response);
    }
}