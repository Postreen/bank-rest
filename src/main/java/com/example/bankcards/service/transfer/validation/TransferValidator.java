package com.example.bankcards.service.transfer.validation;

import com.example.bankcards.dto.transfer.TransferRequest;
import com.example.bankcards.exception.domain.card.SameCardTransferException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class TransferValidator {
    public void validate(TransferRequest transferRequest) {
        if (transferRequest.fromCardId().equals(transferRequest.toCardId())) {
            log.warn("Transfer validation failed: fromCardId equals toCardId cardId={}", transferRequest.fromCardId());
            throw new SameCardTransferException();
        }
    }
}
