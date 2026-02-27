package com.example.bankcards.exception;

import java.time.Instant;

public record ApiErrorDto(
        Instant timestamp,
        int status,
        String error,
        String message,
        String path
) {
    public ApiErrorDto(int status, String error, String message, String path) {
        this(Instant.now(), status, error, message, path);
    }
}
