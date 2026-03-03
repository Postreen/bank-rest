package com.example.bankcards.service.card;

import com.example.bankcards.dto.card.admin.CreateCardRequest;
import com.example.bankcards.entity.CardEntity;
import com.example.bankcards.entity.UserEntity;
import com.example.bankcards.entity.enums.CardStatus;
import com.example.bankcards.exception.domain.card.CardAlreadyBlockedException;
import com.example.bankcards.exception.domain.card.CardNotFoundException;
import com.example.bankcards.exception.domain.card.DuplicatePanException;
import com.example.bankcards.exception.domain.card.InvalidCardStatusException;
import com.example.bankcards.repository.CardRepository;
import com.example.bankcards.service.card.factory.CardFactory;
import com.example.bankcards.service.card.pan.CardPanService;
import com.example.bankcards.service.card.pan.ProtectedPan;
import com.example.bankcards.service.card.validation.Last4Normalizer;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.YearMonth;

@Service
@RequiredArgsConstructor
public class CardService {
    private final CardRepository cardRepository;
    private final CardPanService cardPanService;
    private final CardFactory cardFactory;
    private final Clock clock;

    @Transactional
    public CardEntity createCardForOwner(UserEntity owner, CreateCardRequest request) {
        ProtectedPan protectedPan = cardPanService.protectPan(request.pan());

        if (cardRepository.existsByPanHash(protectedPan.panHash())) {
            throw new DuplicatePanException();
        }

        CardEntity card = cardFactory.newCard(
                owner,
                request,
                protectedPan.encryptedPan(),
                protectedPan.last4(),
                protectedPan.panHash()
        );

        return cardRepository.save(card);
    }

    @Transactional
    public CardEntity getOwnedCardOrThrow(long cardId, long ownerId) {
        CardEntity card = cardRepository.findByIdAndOwnerId(cardId, ownerId)
                .orElseThrow(CardNotFoundException::new);
        applyExpiryIfNeeded(card);
        return card;
    }

    @Transactional
    public Page<CardEntity> searchOwnedCards(long ownerId, CardStatus status, String last4, Pageable pageable) {
        String normalizedLast4 = Last4Normalizer.normalize(last4);

        return cardRepository.searchOwnedCards(ownerId, status, normalizedLast4, status, pageable)
                .map(this::applyExpiryIfNeeded);
    }

    @Transactional
    public void requestCardBlock(CardEntity card) {
        applyExpiryIfNeeded(card);

        if (card.getStatus() == CardStatus.BLOCKED) {
            throw new CardAlreadyBlockedException();
        }
        if (card.getStatus() != CardStatus.ACTIVE) {
            throw new InvalidCardStatusException(card.getStatus());
        }

        card.setStatus(CardStatus.BLOCK_REQUESTED);
    }

    @Transactional
    public void deleteAllCardsByOwnerId(long ownerId) {
        cardRepository.deleteAllByOwnerId(ownerId);
    }

    @Transactional
    public Page<CardEntity> getCards(Pageable pageable) {
        return cardRepository.findAll(pageable).map(this::applyExpiryIfNeeded);
    }

    @Transactional
    public CardEntity changeCardStatus(long cardId, CardStatus status) {
        CardEntity card = getCardOrThrow(cardId);
        applyExpiryIfNeeded(card);
        if (card.getStatus() == CardStatus.EXPIRED && status != CardStatus.EXPIRED) {
            throw new InvalidCardStatusException(card.getStatus());
        }
        card.setStatus(status);
        return card;
    }

    @Transactional
    public void deleteCardById(long cardId) {
        CardEntity card = getCardOrThrow(cardId);
        cardRepository.delete(card);
    }

    @Transactional(readOnly = true)
    public CardEntity getCardOrThrow(long cardId) {
        return cardRepository.findById(cardId)
                .orElseThrow(CardNotFoundException::new);
    }

    public CardEntity loadForUpdate(long ownerId, long cardId) {
        CardEntity card = cardRepository.findWithLockByIdAndOwnerId(cardId, ownerId)
                .orElseThrow(CardNotFoundException::new);
        return applyExpiryIfNeeded(card);
    }

    public CardEntity applyExpiryIfNeeded(CardEntity card) {
        if (card.getStatus() == CardStatus.EXPIRED) {
            return card;
        }

        YearMonth now = YearMonth.now(clock);
        YearMonth expiry = YearMonth.of(card.getExpiryYear(), card.getExpiryMonth());
        if (expiry.isBefore(now)) {
            card.setStatus(CardStatus.EXPIRED);
        }

        return card;
    }
}
