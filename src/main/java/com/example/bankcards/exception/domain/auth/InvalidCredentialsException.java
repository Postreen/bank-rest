package com.example.bankcards.exception.domain.auth;

import com.example.bankcards.exception.domain.DomainException;
import org.springframework.http.HttpStatus;

public class InvalidCredentialsException extends DomainException {
    public InvalidCredentialsException() {
        super("Invalid username or password");
    }

    @Override
    public HttpStatus status() {
        return HttpStatus.UNAUTHORIZED;
    }
}
