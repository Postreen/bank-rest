package com.example.bankcards.exception.domain.card;

import com.example.bankcards.exception.domain.DomainException;
import org.springframework.http.HttpStatus;

public class DuplicatePanException extends DomainException {

    public DuplicatePanException() {
        super("Card with this PAN already exists");
    }

    @Override
    public HttpStatus status() {
        return HttpStatus.CONFLICT;
    }
}
