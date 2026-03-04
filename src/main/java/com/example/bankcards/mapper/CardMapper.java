package com.example.bankcards.mapper;

import com.example.bankcards.dto.card.admin.CardResponse;
import com.example.bankcards.dto.card.user.UserCardResponse;
import com.example.bankcards.entity.CardEntity;
import com.example.bankcards.util.SensitiveDataMasker;
import org.springframework.stereotype.Component;

@Component
public final class CardMapper {
    public CardResponse toCardResponse(CardEntity card) {
        return new CardResponse(
                card.getId(),
                card.getOwner().getId(),
                SensitiveDataMasker.maskPan(card.getPanLast4()),
                card.getHolderName(),
                card.getExpiryMonth(),
                card.getExpiryYear(),
                card.getStatus().name(),
                card.getBalance()
        );
    }

    public UserCardResponse toUserCardResponse(CardEntity card) {
        return new UserCardResponse(
                card.getId(),
                SensitiveDataMasker.maskPan(card.getPanLast4()),
                card.getHolderName(),
                card.getExpiryMonth(),
                card.getExpiryYear(),
                card.getStatus().name(),
                card.getBalance()
        );
    }

}