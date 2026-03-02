package com.example.bankcards.exception.domain.card;

import com.example.bankcards.exception.domain.DomainException;
import org.springframework.http.HttpStatus;

public class InvalidLast4Exception extends DomainException {

    public InvalidLast4Exception() {
        super("last4 must be exactly 4 digits");
    }

    @Override
    public HttpStatus status() {
        return HttpStatus.BAD_REQUEST;
    }
}
