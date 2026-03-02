package com.example.bankcards.service.card;


import com.example.bankcards.dto.card.admin.CardResponse;
import com.example.bankcards.dto.card.admin.CreateCardRequest;
import com.example.bankcards.entity.CardEntity;
import com.example.bankcards.entity.UserEntity;
import com.example.bankcards.entity.enums.CardStatus;
import com.example.bankcards.exception.domain.card.CardNotFoundException;
import com.example.bankcards.exception.domain.card.DuplicatePanException;
import com.example.bankcards.exception.domain.user.UserNotFoundException;
import com.example.bankcards.mapper.CardMapper;
import com.example.bankcards.repository.CardRepository;
import com.example.bankcards.repository.UserRepository;
import com.example.bankcards.security.crypto.CardCryptoService;
import com.example.bankcards.service.card.factory.CardFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CardService {

    private final CardCryptoService cardCryptoService;
    private final CardRepository cardRepository;
    private final UserRepository userRepository;
    private final CardMapper cardMapper;
    private final CardFactory cardFactory;

    @Transactional
    public CardResponse createCard(CreateCardRequest request) {
        UserEntity owner = userRepository.findById(request.ownerId())
                .orElseThrow(UserNotFoundException::new);
        String pan = request.pan();
        String encryptedPan = cardCryptoService.encryptToBase64(pan);
        String panHash = cardCryptoService.hash(pan);

        if (cardRepository.existsByPanHash(panHash)) throw new DuplicatePanException();

        String last4 = pan.substring(pan.length() - 4);
        CardEntity card = cardFactory.newCard(owner, request, encryptedPan, last4, panHash);

        CardEntity saved = cardRepository.save(card);
        return cardMapper.toCardResponse(saved);
    }

    @Transactional(readOnly = true)
    public Page<CardResponse> getCards(Pageable pageable) {
        return cardRepository.findAll(pageable)
                .map(cardMapper::toCardResponse);
    }

    @Transactional
    public CardResponse updateCardStatus(long id, CardStatus status) {
        CardEntity c = getCardOrThrow(id);
        c.setStatus(status);
        return cardMapper.toCardResponse(c);
    }

    @Transactional
    public void deleteCard(long id) {
        CardEntity c = getCardOrThrow(id);
        c.setStatus(CardStatus.DELETED);
    }

    private CardEntity getCardOrThrow(long id) {
        return cardRepository.findById(id)
                .orElseThrow(CardNotFoundException::new);
    }
}