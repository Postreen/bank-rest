package com.example.bankcards.exception.api;

import com.example.bankcards.exception.domain.DomainException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.stream.Collectors;

@RestControllerAdvice
@RequiredArgsConstructor
public class ControllerExceptionHandler {
    private final ApiErrorFactory apiErrorFactory;

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiErrorDto> validation(MethodArgumentNotValidException ex, HttpServletRequest request) {
        String msg = ex.getBindingResult().getFieldErrors().stream()
                .map(this::fmt)
                .collect(Collectors.joining("; "));
        return build(HttpStatus.BAD_REQUEST, msg, request);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiErrorDto> accessDenied(AccessDeniedException ex, HttpServletRequest request) {
        return build(HttpStatus.FORBIDDEN, "Access denied", request);
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ApiErrorDto> authentication(AuthenticationException ex, HttpServletRequest request) {
        return build(HttpStatus.UNAUTHORIZED, "Authentication required", request);
    }

    @ExceptionHandler(DomainException.class)
    public ResponseEntity<ApiErrorDto> domain(DomainException ex, HttpServletRequest request) {
        ApiErrorDto body = apiErrorFactory.of(ex, request.getRequestURI());
        return ResponseEntity.status(ex.status()).body(body);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiErrorDto> illegalArgument(IllegalArgumentException ex, HttpServletRequest request) {
        String msg = defaultMessage(ex.getMessage(), "Invalid argument");
        return build(HttpStatus.BAD_REQUEST, msg, request);
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ApiErrorDto> illegalState(IllegalStateException ex, HttpServletRequest request) {
        String msg = defaultMessage(ex.getMessage(), "Illegal state");
        return build(HttpStatus.CONFLICT, msg, request);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorDto> unexpected(Exception ex, HttpServletRequest request) {
        return build(HttpStatus.INTERNAL_SERVER_ERROR, "Internal server error", request);
    }

    private ResponseEntity<ApiErrorDto> build(HttpStatus status, String message, HttpServletRequest request) {
        ApiErrorDto body = apiErrorFactory.of(status, message, request.getRequestURI());
        return ResponseEntity.status(status).body(body);
    }

    private String defaultMessage(String message, String fallback) {
        return (message == null || message.isBlank()) ? fallback : message;
    }

    private String fmt(FieldError fe) {
        return fe.getField() + ": " + fe.getDefaultMessage();
    }
}
