package com.example.bankcards.service.card;

import com.example.bankcards.dto.card.user.BalanceCardResponse;
import com.example.bankcards.dto.card.user.MyCardResponse;
import com.example.bankcards.entity.CardEntity;
import com.example.bankcards.entity.enums.CardStatus;
import com.example.bankcards.exception.domain.card.*;
import com.example.bankcards.mapper.CardMapper;
import com.example.bankcards.repository.CardRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MyCardService {
    private final CardRepository cardRepository;
    private final CardMapper cardMapper;

    @Transactional(readOnly = true)
    public Page<MyCardResponse> getAllMyCards(long ownerId, CardStatus status, String last4, Pageable pageable) {
        String normalizedLast4 = normalizeLast4(last4);

        return cardRepository.searchMyCards(ownerId, status, normalizedLast4, CardStatus.DELETED, pageable)
                .map(cardMapper::toMyCardResponse);
    }

    @Transactional(readOnly = true)
    public MyCardResponse getMyCard(long ownerId, long cardId) {
        return cardMapper.toMyCardResponse(getMyCardEntity(ownerId, cardId));
    }

    @Transactional(readOnly = true)
    public BalanceCardResponse getCardBalance(long ownerId, long cardId) {
        CardEntity card = getMyCardEntity(ownerId, cardId);
        return new BalanceCardResponse(card.getBalance());
    }

    @Transactional
    public MyCardResponse requestCardBlock(long ownerId, long cardId) {
        CardEntity card = getMyCardEntity(ownerId, cardId);
        ensureNotDeleted(card);

        switch (card.getStatus()) {
            case ACTIVE -> card.setStatus(CardStatus.BLOCK_REQUESTED);
            case BLOCK_REQUESTED -> {
                return cardMapper.toMyCardResponse(card);
            }
            case BLOCKED -> throw new CardAlreadyBlockedException();
            case DELETED -> throw new CardDeletedException();
            default -> throw new InvalidCardStatusException(card.getStatus());
        }

        return cardMapper.toMyCardResponse(card);
    }

    private CardEntity getMyCardEntity(long ownerId, long cardId) {
        return cardRepository.findByIdAndOwnerId(cardId, ownerId)
                .orElseThrow(CardNotFoundException::new);
    }

    private void ensureNotDeleted(CardEntity card) {
        if (card.getStatus() == CardStatus.DELETED) {
            throw new CardDeletedException();
        }
    }

    private String normalizeLast4(String last4) {
        if (last4 == null) return null;

        String v = last4.trim();
        if (v.isEmpty()) return null;

        if (!v.matches("\\d{4}")) {
            throw new InvalidLast4Exception();
        }

        return v;
    }
}
