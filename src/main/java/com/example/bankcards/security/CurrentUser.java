package com.example.bankcards.security;

import com.example.bankcards.security.jwt.JwtPrincipal;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

@Component
public class CurrentUser {
    public JwtPrincipal require(Authentication authentication) {
        return (JwtPrincipal) authentication.getPrincipal();
    }
}
