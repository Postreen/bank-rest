package com.example.bankcards.mapper;

import com.example.bankcards.dto.card.CardResponse;
import com.example.bankcards.entity.CardEntity;

public final class CardMapper {

    public static CardResponse toResponse(CardEntity cardEntity) {
        return new CardResponse(
                cardEntity.getId(),
                cardEntity.getOwner().getId(),
                "**** **** **** " + cardEntity.getPanLast4(),
                cardEntity.getHolderName(),
                cardEntity.getExpiryMonth(),
                cardEntity.getExpiryYear(),
                cardEntity.getStatus().name(),
                cardEntity.getBalance()
        );
    }
}