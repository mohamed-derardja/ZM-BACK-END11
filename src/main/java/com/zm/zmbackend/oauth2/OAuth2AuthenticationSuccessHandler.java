package com.zm.zmbackend.oauth2;

import com.zm.zmbackend.services.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;

@Component
public class OAuth2AuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final UserService userService;

    @Autowired
    @Lazy
    public OAuth2AuthenticationSuccessHandler(UserService userService) {
        this.userService = userService;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
        if (response.isCommitted()) {
            return;
        }

        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        
        // Generate token for the authenticated user
        String token = userService.generateToken(userPrincipal.getId());
        
        // Build the redirect URL with the token
        String redirectUrl = UriComponentsBuilder.fromUriString("http://localhost:3000/oauth2/redirect")
                .queryParam("token", token)
                .queryParam("userId", userPrincipal.getId())
                .build().toUriString();
        
        getRedirectStrategy().sendRedirect(request, response, redirectUrl);
    }
}