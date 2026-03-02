package com.example.bankcards.dto.card.admin;

public record AdminUserResponse(
        Long id,
        String username,
        String role,
        boolean enabled
) {}
