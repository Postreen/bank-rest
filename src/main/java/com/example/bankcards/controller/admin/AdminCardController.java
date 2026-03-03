package com.example.bankcards.controller.admin;

import com.example.bankcards.dto.card.admin.CardResponse;
import com.example.bankcards.dto.card.admin.CreateCardRequest;
import com.example.bankcards.dto.card.admin.UpdateCardStatusRequest;
import com.example.bankcards.service.admin.AdminCardService;
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

    private final AdminCardService adminCardService;

    @GetMapping
    public Page<CardResponse> getCards(@ParameterObject Pageable pageable) {
        return adminCardService.getCards(pageable);
    }

    @PostMapping("/create")
    public CardResponse createCard(@RequestBody @Valid CreateCardRequest request) {
        return adminCardService.createCardForOwner(request);
    }

    @PatchMapping("/{id}/status")
    public CardResponse changeCardStatus(@PathVariable Long id,
                                     @RequestBody @Valid UpdateCardStatusRequest request) {
        return adminCardService.changeCardStatus(id, request.status());
    }

    @DeleteMapping("/{id}")
    public void deleteCardById(@PathVariable Long id) {
        adminCardService.deleteCardById(id);
    }
}
