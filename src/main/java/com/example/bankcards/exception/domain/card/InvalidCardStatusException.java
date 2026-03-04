package com.example.bankcards.exception.domain.card;

import com.example.bankcards.entity.enums.CardStatus;
import com.example.bankcards.exception.domain.DomainException;
import org.springframework.http.HttpStatus;

public class InvalidCardStatusException extends DomainException {

    public InvalidCardStatusException(CardStatus status) {
        super("Invalid card status: " + status);
    }

    @Override
    public HttpStatus status() {
        return HttpStatus.CONFLICT;
    }
}
