package com.example.bankcards.exception.domain.card;

import com.example.bankcards.exception.domain.DomainException;
import org.springframework.http.HttpStatus;

public class InvalidPanException extends DomainException {

    public InvalidPanException() {
        super("Invalid PAN");
    }

    @Override
    public HttpStatus status() {
        return HttpStatus.BAD_REQUEST;
    }
}
