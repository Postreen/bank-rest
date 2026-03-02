package com.example.bankcards.dto.auth;

public record LoginResult(
        String token,
        long expiresIn
) {
}
