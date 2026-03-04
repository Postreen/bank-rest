package com.example.bankcards.exception.domain.card;

import com.example.bankcards.exception.domain.DomainException;
import org.springframework.http.HttpStatus;

public class CardAlreadyBlockedException extends DomainException {

    public CardAlreadyBlockedException() {
        super("Card already blocked");
    }

    @Override
    public HttpStatus status() {
        return HttpStatus.CONFLICT;
    }
}
