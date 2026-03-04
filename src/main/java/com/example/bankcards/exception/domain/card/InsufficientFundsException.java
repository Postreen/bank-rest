package com.example.bankcards.exception.domain.card;

import com.example.bankcards.exception.domain.DomainException;
import org.springframework.http.HttpStatus;

public class InsufficientFundsException extends DomainException {

    public InsufficientFundsException() {
        super("Insufficient funds");
    }

    @Override
    public HttpStatus status() {
        return HttpStatus.CONFLICT;
    }
}
