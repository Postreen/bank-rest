package com.example.bankcards.controller.user;

import com.example.bankcards.dto.card.user.TransferRequest;
import com.example.bankcards.dto.card.user.TransferResponse;
import com.example.bankcards.security.CurrentUser;
import com.example.bankcards.service.transfer.TransferService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/transfers")
@RequiredArgsConstructor
public class TransferController {
    private final TransferService transferService;
    private final CurrentUser currentUser;

    @PostMapping
    public TransferResponse transfer(
            @RequestBody @Valid TransferRequest transferRequest,
            Authentication authentication
    ) {
        long userId = currentUser.require(authentication).userId();
        return transferService.transfer(userId, transferRequest);
    }
}
