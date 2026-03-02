package com.example.bankcards.service.card;

import com.example.bankcards.dto.card.api.BalanceResponse;
import com.example.bankcards.dto.card.api.MyCardResponse;
import com.example.bankcards.entity.CardEntity;
import com.example.bankcards.entity.enums.CardStatus;
import com.example.bankcards.mapper.CardMapper;
import com.example.bankcards.repository.CardRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MyCardService {
    private final CardRepository cardRepository;
    private final CardMapper cardMapper;

    public Page<MyCardResponse> search(long ownerId, CardStatus status, String last4, Pageable pageable) {
        String normalizedLast4 = (last4 == null || last4.isBlank()) ? null : last4.trim();
        return cardRepository.searchMyCards(ownerId, status, normalizedLast4, pageable)
                .map(cardMapper::toMyCardResponse);
    }

    public MyCardResponse getMyCard(long ownerId, long cardId) {
        return cardMapper.toMyCardResponse(getMyCardEntity(ownerId, cardId));
    }

    public BalanceResponse getBalance(long ownerId, long cardId) {
        CardEntity card = getMyCardEntity(ownerId, cardId);
        return new BalanceResponse(card.getBalance());
    }

    private CardEntity getMyCardEntity(long ownerId, long cardId) {
        return cardRepository.findMyCardById(ownerId, cardId)
                .orElseThrow(() -> new EntityNotFoundException("Card not found"));
    }
}
