package com.example.bankcards.exception;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

@Component
public class ApiErrorFactory {

    public ApiErrorDto of(HttpStatus status, String message, String path) {
        return new ApiErrorDto(status.value(), status.name(), message, path);
    }

    public ApiErrorDto unauthorized(String message, String path) {
        return of(HttpStatus.UNAUTHORIZED, message, path);
    }

    public ApiErrorDto forbidden(String message, String path) {
        return of(HttpStatus.FORBIDDEN, message, path);
    }

    public ApiErrorDto badRequest(String message, String path) {
        return of(HttpStatus.BAD_REQUEST, message, path);
    }
}
