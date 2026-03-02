package com.example.bankcards.service.card;


import com.example.bankcards.dto.card.admin.CardResponse;
import com.example.bankcards.dto.card.admin.CreateCardRequest;
import com.example.bankcards.entity.CardEntity;
import com.example.bankcards.entity.UserEntity;
import com.example.bankcards.entity.enums.CardStatus;
import com.example.bankcards.exception.domain.card.CardNotFoundException;
import com.example.bankcards.exception.domain.user.UserNotFoundException;
import com.example.bankcards.mapper.CardMapper;
import com.example.bankcards.repository.CardRepository;
import com.example.bankcards.repository.UserRepository;
import com.example.bankcards.security.crypto.CardCryptoService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Service
@RequiredArgsConstructor
public class CardService {

    private final CardCryptoService cardCryptoService;
    private final CardRepository cardRepository;
    private final UserRepository userRepository;
    private final CardMapper cardMapper;

    public CardResponse createCard(CreateCardRequest request) {
        UserEntity owner = userRepository.findById(request.ownerId())
                .orElseThrow(UserNotFoundException::new);

        String pan = normalizePan(request.pan());

        CardEntity card = new CardEntity();
        card.setOwner(owner);

        card.setEncryptedPan(cardCryptoService.encryptToBase64(pan));
        card.setPanLast4(pan.substring(pan.length() - 4));

        card.setHolderName(request.holderName());
        card.setExpiryMonth(request.expiryMonth());
        card.setExpiryYear(request.expiryYear());
        card.setStatus(CardStatus.ACTIVE);
        card.setBalance(BigDecimal.ZERO);
        card.setCreatedAt(OffsetDateTime.now());

        CardEntity saved = cardRepository.save(card);
        return cardMapper.toCardResponse(saved);
    }

    public Page<CardResponse> getAllCards(Pageable pageable) {
        return cardRepository.findAll(pageable).map(cardMapper::toCardResponse);
    }

    public CardResponse updateCardStatus(Long id, CardStatus status) {
        CardEntity c = getEntity(id);
        c.setStatus(status);
        return cardMapper.toCardResponse(cardRepository.save(c));
    }

    public void deleteCard(Long id) {
        CardEntity c = getEntity(id);
        c.setStatus(CardStatus.DELETED);
        cardRepository.save(c);
    }

    private CardEntity getEntity(Long id) {
        return cardRepository.findById(id)
                .orElseThrow(CardNotFoundException::new);
    }

    private String normalizePan(String pan) {
        return pan == null ? "" : pan.replaceAll("\\s+", "");
    }
}