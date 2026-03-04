package com.example.bankcards.dto.auth;

public record MeResponse(
        long id,
        String username,
        String role
) {
}
