package com.example.bankcards.exception.domain.card;

import com.example.bankcards.exception.domain.DomainException;
import org.springframework.http.HttpStatus;

public class SameCardTransferException extends DomainException {

    public SameCardTransferException() {
        super("Source and destination cards must be different");
    }

    @Override
    public HttpStatus status() {
        return HttpStatus.BAD_REQUEST;
    }
}
