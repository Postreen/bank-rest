package com.example.bankcards.dto.card.admin;

import java.math.BigDecimal;

public record CardResponse(
        Long id,
        Long ownerId,
        String maskedPan,
        String holderName,
        int expiryMonth,
        int expiryYear,
        String status,
        BigDecimal balance
) {
}
