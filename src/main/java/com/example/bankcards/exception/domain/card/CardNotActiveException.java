package com.example.bankcards.exception.domain.card;

import com.example.bankcards.exception.domain.DomainException;
import org.springframework.http.HttpStatus;

public class CardNotActiveException extends DomainException {

    public CardNotActiveException() {
        super("Card must be ACTIVE");
    }

    @Override
    public HttpStatus status() {
        return HttpStatus.CONFLICT;
    }
}
