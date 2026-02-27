package com.example.bankcards.dto.auth;

public record LoginResponse(
        String accessToken,
        String tokenType,
        long expiresIn
) {
}
