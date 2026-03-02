package com.example.bankcards.controller;

import com.example.bankcards.dto.card.admin.CardResponse;
import com.example.bankcards.dto.card.admin.CreateCardRequest;
import com.example.bankcards.dto.card.admin.UpdateCardStatusRequest;
import com.example.bankcards.service.card.CardService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/cards")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminCardController {

    private final CardService cardService;

    @PostMapping
    public CardResponse createCard(@RequestBody @Valid CreateCardRequest request) {
        return cardService.createCard(request);
    }

    @GetMapping
    public Page<CardResponse> getAllCards(@ParameterObject Pageable pageable) {
        return cardService.getAllCards(pageable);
    }

    @PatchMapping("/{id}/status")
    public CardResponse updateCardStatus(@PathVariable Long id,
                                     @RequestBody @Valid UpdateCardStatusRequest request) {
        return cardService.updateCardStatus(id, request.status());
    }

    @DeleteMapping("/{id}")
    public void deleteCard(@PathVariable Long id) {
        cardService.deleteCard(id);
    }
}
