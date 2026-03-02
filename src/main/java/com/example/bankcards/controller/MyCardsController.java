package com.example.bankcards.controller;

import com.example.bankcards.dto.card.user.BalanceCardResponse;
import com.example.bankcards.dto.card.user.MyCardResponse;
import com.example.bankcards.entity.enums.CardStatus;
import com.example.bankcards.security.CurrentUser;
import com.example.bankcards.service.card.MyCardService;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/cards")
@RequiredArgsConstructor
public class MyCardsController {

    private final MyCardService myCardService;
    private final CurrentUser currentUser;

    @GetMapping
    public Page<MyCardResponse> getAllMyCards(
            @RequestParam(required = false) CardStatus status,
            @RequestParam(required = false) String last4,
            @ParameterObject Pageable pageable,
            Authentication authentication
    ) {
        long userId = currentUser.require(authentication).userId();
        return myCardService.getAllMyCards(userId, status, last4, pageable);
    }

    @GetMapping("/{id}")
    public MyCardResponse getMyCard(
            @PathVariable long id,
            Authentication authentication
    ) {
        long userId = currentUser.require(authentication).userId();
        return myCardService.getMyCard(userId, id);
    }

    @GetMapping("/{id}/balance")
    public BalanceCardResponse getCardBalance(
            @PathVariable long id,
            Authentication authentication
    ) {
        long userId = currentUser.require(authentication).userId();
        return myCardService.getCardBalance(userId, id);
    }

    @PostMapping("/{id}/block-request")
    public MyCardResponse requestCardBlock(
            @PathVariable long id,
            Authentication authentication
    ) {
        long userId = currentUser.require(authentication).userId();
        return myCardService.requestCardBlock(userId, id);
    }
}
