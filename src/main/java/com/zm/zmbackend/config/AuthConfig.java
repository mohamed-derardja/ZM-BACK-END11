package com.zm.zmbackend.config;

import com.zm.zmbackend.filter.AuthenticationFilter;
import com.zm.zmbackend.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class AuthConfig implements WebMvcConfigurer {

    private final UserService userService;

    @Autowired
    public AuthConfig(UserService userService) {
        this.userService = userService;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(authenticationFilter())
                .addPathPatterns("/api/users/**")
                .excludePathPatterns("/api/users/login", "/api/users/register");
    }

    @Bean
    public AuthenticationFilter authenticationFilter() {
        return new AuthenticationFilter(userService);
    }
}