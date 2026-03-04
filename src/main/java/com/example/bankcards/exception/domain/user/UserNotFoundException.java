package com.example.bankcards.exception.domain.user;

import com.example.bankcards.exception.domain.DomainException;
import org.springframework.http.HttpStatus;

public class UserNotFoundException extends DomainException {

    public UserNotFoundException() {
        super("User not found");
    }

    @Override
    public HttpStatus status() {
        return HttpStatus.NOT_FOUND;
    }
}
