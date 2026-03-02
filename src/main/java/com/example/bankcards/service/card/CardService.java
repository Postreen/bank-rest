package com.example.bankcards.service.card;


import com.example.bankcards.dto.card.CreateCardRequest;
import com.example.bankcards.entity.CardEntity;
import com.example.bankcards.entity.UserEntity;
import com.example.bankcards.entity.enums.CardStatus;
import com.example.bankcards.repository.CardRepository;
import com.example.bankcards.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Service
@RequiredArgsConstructor
public class CardService {

    private final CardRepository cardRepository;
    private final UserRepository userRepository;

    public CardEntity create(CreateCardRequest request) {
        UserEntity owner = userRepository.findById(request.ownerId())
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        CardEntity card = new CardEntity();

        card.setOwner(owner);
        card.setEncryptedPan(request.pan());
        card.setPanLast4(request.pan().substring(request.pan().length() - 4));
        card.setHolderName(request.holderName());
        card.setExpiryMonth(request.expiryMonth());
        card.setExpiryYear(request.expiryYear());
        card.setStatus(CardStatus.ACTIVE);
        card.setBalance(BigDecimal.ZERO);
        card.setCreatedAt(OffsetDateTime.now());

        return cardRepository.save(card);
    }

    public Page<CardEntity> findAll(Pageable pageable) {
        return cardRepository.findAll(pageable);
    }

    public CardEntity get(Long id) {
        return cardRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Card not found"));
    }

    public CardEntity updateStatus(Long id, CardStatus status) {
        CardEntity c = get(id);
        c.setStatus(status);
        return cardRepository.save(c);
    }

    public void delete(Long id) {
        CardEntity c = get(id);
        c.setStatus(CardStatus.DELETED);
        cardRepository.save(c);
    }
}
