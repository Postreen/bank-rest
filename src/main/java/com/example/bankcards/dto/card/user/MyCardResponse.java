package com.example.bankcards.dto.card.user;

import java.math.BigDecimal;

public record MyCardResponse(
     Long id,
     String maskedPan,
     String holderName,
     int expiryMonth,
     int expiryYear,
     String status,
     BigDecimal balance
) {
}
