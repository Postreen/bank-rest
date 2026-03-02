package com.example.bankcards.dto.card;

import com.example.bankcards.entity.CardEntity;
import com.example.bankcards.mapper.CardMapper;

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
    public static CardResponse from(CardEntity cardEntity) {
        return CardMapper.toResponse(cardEntity);
    }
}
