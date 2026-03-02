package com.example.bankcards.service.card.factory;

import com.example.bankcards.dto.card.admin.CreateCardRequest;
import com.example.bankcards.entity.CardEntity;
import com.example.bankcards.entity.UserEntity;
import com.example.bankcards.entity.enums.CardStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.OffsetDateTime;

@Service
@RequiredArgsConstructor
public class CardFactory {
    private final Clock clock;

    public CardEntity newCard(UserEntity owner,
                              CreateCardRequest request,
                              String encryptedPan,
                              String panLast4,
                              String panHash) {

        CardEntity card = new CardEntity();
        card.setOwner(owner);

        card.setEncryptedPan(encryptedPan);
        card.setPanLast4(panLast4);
        card.setPanHash(panHash);

        card.setHolderName(request.holderName());
        card.setExpiryMonth(request.expiryMonth());
        card.setExpiryYear(request.expiryYear());

        card.setStatus(CardStatus.ACTIVE);
        card.setBalance(BigDecimal.ZERO);
        card.setCreatedAt(OffsetDateTime.now(clock));
        return card;
    }
}
