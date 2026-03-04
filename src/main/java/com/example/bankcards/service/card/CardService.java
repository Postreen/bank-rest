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
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.YearMonth;

@Slf4j
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
            log.warn("Card creation rejected: duplicate PAN hash for ownerId={}", owner.getId());
            throw new DuplicatePanException();
        }

        CardEntity card = cardFactory.newCard(
                owner,
                request,
                protectedPan.encryptedPan(),
                protectedPan.last4(),
                protectedPan.panHash()
        );

        CardEntity saved = cardRepository.save(card);
        log.info("Card persisted: cardId={} ownerId={} status={}", saved.getId(), owner.getId(), saved.getStatus());
        return saved;
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
            log.warn("Block request rejected: card already blocked cardId={}", card.getId());
            throw new CardAlreadyBlockedException();
        }
        if (card.getStatus() != CardStatus.ACTIVE) {
            log.warn("Block request rejected: invalid status cardId={} status={}", card.getId(), card.getStatus());
            throw new InvalidCardStatusException(card.getStatus());
        }

        card.setStatus(CardStatus.BLOCK_REQUESTED);
        log.info("Block request accepted: cardId={} status={}", card.getId(), card.getStatus());
    }

    @Transactional
    public void deleteAllCardsByOwnerId(long ownerId) {
        cardRepository.deleteAllByOwnerId(ownerId);
        log.info("Deleted all cards for ownerId={}", ownerId);
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
            log.warn("Card status change rejected: expired cardId={} attemptedStatus={}", cardId, status);
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

    @Transactional
    public CardEntity loadForUpdate(long cardId, long ownerId) {
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
            log.info("Card expired automatically: cardId={}", card.getId());
        }

        return card;
    }
}
