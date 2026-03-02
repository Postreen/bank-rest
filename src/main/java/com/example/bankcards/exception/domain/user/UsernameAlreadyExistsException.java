package com.example.bankcards.exception.domain.user;

import com.example.bankcards.exception.domain.DomainException;
import org.springframework.http.HttpStatus;

public class UsernameAlreadyExistsException extends DomainException {

    public UsernameAlreadyExistsException(String username) {
        super("Username already exists: " + username);
    }

    @Override
    public HttpStatus status() {
        return HttpStatus.CONFLICT;
    }
}
