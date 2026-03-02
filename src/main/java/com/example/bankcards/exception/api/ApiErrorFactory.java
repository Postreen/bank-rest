package com.example.bankcards.exception.api;

import com.example.bankcards.exception.domain.DomainException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

@Component
public class ApiErrorFactory {

    public ApiErrorDto of(HttpStatus status, String message, String path) {
        return new ApiErrorDto(status.value(), status.name(), message, path);
    }

    public ApiErrorDto of(DomainException ex, String path) {
        HttpStatus status = ex.status();
        String msg = (ex.getMessage() == null || ex.getMessage().isBlank())
                ? "Request failed"
                : ex.getMessage();
        return of(status, msg, path);
    }
}
