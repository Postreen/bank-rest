package com.example.bankcards.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class CardOperationException extends RuntimeException {

    private final HttpStatus status;

    public CardOperationException(HttpStatus status, String message) {
        super(message);
        this.status = status;
    }
}
