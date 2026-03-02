package com.example.bankcards.exception.domain.card;

import com.example.bankcards.exception.domain.DomainException;
import org.springframework.http.HttpStatus;

public class InvalidTransferAmountException extends DomainException {

    public InvalidTransferAmountException() {
        super("Transfer amount must be greater than 0");
    }

    @Override
    public HttpStatus status() {
        return HttpStatus.BAD_REQUEST;
    }
}
