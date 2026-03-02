package com.example.bankcards.service.transfer.validation;

import com.example.bankcards.dto.card.user.TransferRequest;
import com.example.bankcards.exception.domain.card.SameCardTransferException;
import org.springframework.stereotype.Component;

@Component
public class TransferValidator {
    public void validate(TransferRequest transferRequest) {
        if (transferRequest.fromCardId().equals(transferRequest.toCardId())) {
            throw new SameCardTransferException();
        }
    }
}
