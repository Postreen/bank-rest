package com.example.bankcards.security.jwt;

import com.example.bankcards.entity.enums.Role;

public record JwtPrincipal(
        long userId,
        String username,
        Role role
) {}
