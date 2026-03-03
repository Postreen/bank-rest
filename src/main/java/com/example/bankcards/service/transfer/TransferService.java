package com.example.bankcards.service.transfer;

import com.example.bankcards.dto.transfer.TransferRequest;
import com.example.bankcards.dto.transfer.TransferResponse;
import com.example.bankcards.entity.CardEntity;
import com.example.bankcards.service.card.CardService;
import com.example.bankcards.service.transfer.validation.TransferValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class TransferService {

    private final TransferValidator validator;
    private final CardService cardService;
    private final TransferDomainService domain;

    @Transactional
    public TransferResponse transfer(long ownerId, TransferRequest req) {
        validator.validate(req);

        BigDecimal amount = req.amount();

        long firstId = Math.min(req.fromCardId(), req.toCardId());
        long secondId = Math.max(req.fromCardId(), req.toCardId());

        CardEntity first = cardService.loadForUpdate(ownerId, firstId);
        CardEntity second = cardService.loadForUpdate(ownerId, secondId);

        CardEntity from = (req.fromCardId() == first.getId()) ? first : second;
        CardEntity to = (req.toCardId() == first.getId()) ? first : second;

        domain.execute(from, to, amount);

        return new TransferResponse(
                from.getId(),
                to.getId(),
                amount,
                from.getBalance(),
                to.getBalance()
        );
    }
}