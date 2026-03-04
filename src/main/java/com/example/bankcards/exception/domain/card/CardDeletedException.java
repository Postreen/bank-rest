package com.example.bankcards.exception.domain.card;

import com.example.bankcards.exception.domain.DomainException;
import org.springframework.http.HttpStatus;

public class CardDeletedException extends DomainException {

    public CardDeletedException() {
        super("Card deleted");
    }

    @Override
    public HttpStatus status() {
        return HttpStatus.GONE;
    }
}
