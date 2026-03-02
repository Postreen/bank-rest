package com.example.bankcards.service.transfer.load;

import com.example.bankcards.entity.CardEntity;
import com.example.bankcards.entity.enums.CardStatus;
import com.example.bankcards.exception.domain.card.*;
import com.example.bankcards.repository.CardRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OwnedCardLoader {

    private final CardRepository cardRepository;

    public CardEntity loadForUpdate(long ownerId, long cardId) {
        return cardRepository.findForUpdateByIdAndOwnerIdAndStatusNot(cardId, ownerId, CardStatus.DELETED)
                .orElseThrow(CardNotFoundException::new);
    }
}
