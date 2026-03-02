package com.example.bankcards.controller;

import com.example.bankcards.dto.card.CardResponse;
import com.example.bankcards.dto.card.CreateCardRequest;
import com.example.bankcards.dto.card.UpdateStatusRequest;
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
    public CardResponse create(@RequestBody @Valid CreateCardRequest request) {
        return CardResponse.from(cardService.create(request));
    }

    @GetMapping
    public Page<CardResponse> all(@ParameterObject Pageable pageable) {
        return cardService.findAll(pageable).map(CardResponse::from);
    }

    @PatchMapping("/{id}/status")
    public CardResponse updateStatus(
            @PathVariable Long id,
            @RequestBody @Valid UpdateStatusRequest request
    ) {
        return CardResponse.from(cardService.updateStatus(id, request.status()));
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        cardService.delete(id);
    }
}
