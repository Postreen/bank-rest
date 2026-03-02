package com.example.bankcards.service.card.pan;

public record ProtectedPan(
        String encryptedPan,
        String panHash,
        String last4
) {
}
