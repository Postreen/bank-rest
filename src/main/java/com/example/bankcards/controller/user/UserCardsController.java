package com.example.bankcards.controller.user;

import com.example.bankcards.dto.card.user.BalanceCardResponse;
import com.example.bankcards.dto.card.user.UserCardResponse;
import com.example.bankcards.entity.enums.CardStatus;
import com.example.bankcards.security.CurrentUser;
import com.example.bankcards.service.user.UserCardService;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/cards")
@RequiredArgsConstructor
public class UserCardsController {

    private final UserCardService userCardService;
    private final CurrentUser currentUser;

    @GetMapping
    public Page<UserCardResponse> getAllMyCards(
            @RequestParam(required = false) CardStatus status,
            @RequestParam(required = false) String last4,
            @ParameterObject Pageable pageable,
            Authentication authentication
    ) {
        long userId = currentUser.require(authentication).userId();
        return userCardService.getAllOwnedCards(userId, status, last4, pageable);
    }

    @GetMapping("/{id}")
    public UserCardResponse getMyCard(
            @PathVariable long id,
            Authentication authentication
    ) {
        long userId = currentUser.require(authentication).userId();
        return userCardService.getOwnedCard(id, userId);
    }

    @GetMapping("/{id}/balance")
    public BalanceCardResponse getCardBalance(
            @PathVariable long id,
            Authentication authentication
    ) {
        long userId = currentUser.require(authentication).userId();
        return userCardService.getCardBalance(id, userId);
    }

    @PostMapping("/{id}/block-request")
    public UserCardResponse requestCardBlock(
            @PathVariable long id,
            Authentication authentication
    ) {
        long userId = currentUser.require(authentication).userId();
        return userCardService.requestCardBlock(id, userId);
    }
}
