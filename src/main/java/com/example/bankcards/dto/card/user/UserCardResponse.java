package com.example.bankcards.dto.card.user;

import java.math.BigDecimal;

public record UserCardResponse(
     Long id,
     String maskedPan,
     String holderName,
     int expiryMonth,
     int expiryYear,
     String status,
     BigDecimal balance
) {
}
