package com.example.bankcards.service.admin;


import com.example.bankcards.dto.card.admin.CardResponse;
import com.example.bankcards.dto.card.admin.CreateCardRequest;
import com.example.bankcards.entity.CardEntity;
import com.example.bankcards.entity.UserEntity;
import com.example.bankcards.entity.enums.CardStatus;
import com.example.bankcards.mapper.CardMapper;
import com.example.bankcards.service.card.CardService;
import com.example.bankcards.service.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AdminCardService {

    private final UserService userService;
    private final CardService cardService;
    private final CardMapper cardMapper;

    @Transactional
    public CardResponse createCardForOwner(CreateCardRequest request) {
        UserEntity owner = userService.getUserOrThrow(request.ownerId());

        CardEntity card = cardService.createCardForOwner(owner, request);
        return cardMapper.toCardResponse(card);
    }

    @Transactional(readOnly = true)
    public Page<CardResponse> getCards(Pageable pageable) {
        return cardService.getCards(pageable)
                .map(cardMapper::toCardResponse);
    }

    @Transactional
    public CardResponse changeCardStatus(long id, CardStatus status) {
        CardEntity updated = cardService.changeCardStatus(id, status);
        return cardMapper.toCardResponse(updated);
    }

    @Transactional
    public void deleteCardById(long id) {
        cardService.deleteCardById(id);
    }
}