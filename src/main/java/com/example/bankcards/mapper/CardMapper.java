package com.example.bankcards.mapper;

import com.example.bankcards.dto.card.CardResponse;
import com.example.bankcards.dto.card.api.MyCardResponse;
import com.example.bankcards.entity.CardEntity;
import org.springframework.stereotype.Component;

@Component
public final class CardMapper {
    public CardResponse toCardResponse(CardEntity card) {
        return new CardResponse(
                card.getId(),
                card.getOwner().getId(),
                mask(card.getPanLast4()),
                card.getHolderName(),
                card.getExpiryMonth(),
                card.getExpiryYear(),
                card.getStatus().name(),
                card.getBalance()
        );
    }

    public MyCardResponse toMyCardResponse(CardEntity card) {
        return new MyCardResponse(
                card.getId(),
                mask(card.getPanLast4()),
                card.getHolderName(),
                card.getExpiryMonth(),
                card.getExpiryYear(),
                card.getStatus().name(),
                card.getBalance()
        );
    }

    private String mask(String last4) {
        return "**** **** **** " + last4;
    }
}