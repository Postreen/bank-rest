package com.example.bankcards.service.card;

import com.example.bankcards.dto.card.api.BalanceResponse;
import com.example.bankcards.dto.card.api.MyCardResponse;
import com.example.bankcards.entity.CardEntity;
import com.example.bankcards.entity.enums.CardStatus;
import com.example.bankcards.exception.CardOperationException;
import com.example.bankcards.mapper.CardMapper;
import com.example.bankcards.repository.CardRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MyCardService {
    private final CardRepository cardRepository;
    private final CardMapper cardMapper;

    public Page<MyCardResponse> getAllMyCards(long ownerId, CardStatus status, String last4, Pageable pageable) {
        String normalizedLast4 = (last4 == null || last4.isBlank()) ? null : last4.trim();

        return cardRepository.searchMyCards(ownerId, status, normalizedLast4, CardStatus.DELETED, pageable)
                .map(cardMapper::toMyCardResponse);
    }

    public MyCardResponse getMyCard(long ownerId, long cardId) {
        return cardMapper.toMyCardResponse(getMyCardEntity(ownerId, cardId));
    }

    public BalanceResponse getCardBalance(long ownerId, long cardId) {
        CardEntity card = getMyCardEntity(ownerId, cardId);
        return new BalanceResponse(card.getBalance());
    }

    private CardEntity getMyCardEntity(long ownerId, long cardId) {
        return cardRepository.findMyCardById(ownerId, cardId, CardStatus.DELETED)
                .orElseThrow(() -> new EntityNotFoundException("Card not found"));
    }

    @Transactional
    public MyCardResponse requestCardBlock(long ownerId, long cardId) {

        CardEntity card = cardRepository
                .findMyCardById(ownerId, cardId, CardStatus.DELETED)
                .orElseThrow(() -> new EntityNotFoundException("Card not found"));

        switch (card.getStatus()) {
            case ACTIVE -> card.setStatus(CardStatus.BLOCK_REQUESTED);
            case BLOCK_REQUESTED -> {}
            case BLOCKED ->
                    throw new CardOperationException(HttpStatus.CONFLICT, "Card already blocked");
            case DELETED ->
                    throw new CardOperationException(HttpStatus.GONE, "Card deleted");
            default ->
                    throw new CardOperationException(HttpStatus.CONFLICT, "Invalid card status: " + card.getStatus());
        }

        return cardMapper.toMyCardResponse(card);
    }
}
