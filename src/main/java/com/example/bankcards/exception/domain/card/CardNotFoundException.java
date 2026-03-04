package com.example.bankcards.exception.domain.card;

import com.example.bankcards.exception.domain.DomainException;
import org.springframework.http.HttpStatus;

public class CardNotFoundException extends DomainException {

    public CardNotFoundException() {
        super("Card not found");
    }

    @Override
    public HttpStatus status() {
        return HttpStatus.NOT_FOUND;
    }
}
