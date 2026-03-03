package com.example.bankcards.service.user;

import com.example.bankcards.dto.card.user.BalanceCardResponse;
import com.example.bankcards.dto.card.user.UserCardResponse;
import com.example.bankcards.entity.CardEntity;
import com.example.bankcards.entity.enums.CardStatus;
import com.example.bankcards.mapper.CardMapper;
import com.example.bankcards.service.card.CardService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserCardService {
    private final CardService cardService;
    private final CardMapper cardMapper;

    @Transactional(readOnly = true)
    public Page<UserCardResponse> getAllOwnedCards(long ownerId, CardStatus status, String last4, Pageable pageable) {
        return cardService.searchOwnedCards(ownerId, status, last4, pageable)
                .map(cardMapper::toUserCardResponse);
    }

    @Transactional(readOnly = true)
    public UserCardResponse getOwnedCard(long cardId, long ownerId) {
        CardEntity card = cardService.getOwnedCardOrThrow(cardId, ownerId);
        return cardMapper.toUserCardResponse(card);
    }

    @Transactional(readOnly = true)
    public BalanceCardResponse getCardBalance(long cardId, long ownerId) {
        CardEntity card = cardService.getOwnedCardOrThrow(cardId, ownerId);
        return new BalanceCardResponse(card.getBalance());
    }

    @Transactional
    public UserCardResponse requestCardBlock(long cardId, long ownerId) {
        CardEntity card = cardService.getOwnedCardOrThrow(cardId, ownerId);

        cardService.requestCardBlock(card);

        return cardMapper.toUserCardResponse(card);
    }
}
