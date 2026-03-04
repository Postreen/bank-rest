package com.example.bankcards.exception.api;

import com.example.bankcards.exception.domain.DomainException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
@RequiredArgsConstructor
public class ControllerExceptionHandler {
    private final ApiErrorFactory apiErrorFactory;

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiErrorDto> validation(MethodArgumentNotValidException ex, HttpServletRequest request) {
        String msg = ex.getBindingResult().getFieldErrors().stream()
                .map(this::fmt)
                .collect(Collectors.joining("; "));
        log.warn("Validation error: path={} correlationId={}", request.getRequestURI(), correlationId());
        return build(HttpStatus.BAD_REQUEST, msg, request);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiErrorDto> accessDenied(HttpServletRequest request) {
        log.warn("Access denied: path={} correlationId={}", request.getRequestURI(), correlationId());
        return build(HttpStatus.FORBIDDEN, "Access denied", request);
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ApiErrorDto> authentication(HttpServletRequest request) {
        log.warn("Authentication error: path={} correlationId={}", request.getRequestURI(), correlationId());
        return build(HttpStatus.UNAUTHORIZED, "Authentication required", request);
    }

    @ExceptionHandler(DomainException.class)
    public ResponseEntity<ApiErrorDto> domain(DomainException ex, HttpServletRequest request) {
        log.warn("Domain error: type={} path={} correlationId={}", ex.getClass().getSimpleName(), request.getRequestURI(), correlationId());
        ApiErrorDto body = apiErrorFactory.of(ex, request.getRequestURI());
        return ResponseEntity.status(ex.status()).body(body);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiErrorDto> illegalArgument(IllegalArgumentException ex, HttpServletRequest request) {
        log.warn("Illegal argument: path={} correlationId={}", request.getRequestURI(), correlationId());
        return build(HttpStatus.BAD_REQUEST, "Invalid argument", request);
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ApiErrorDto> illegalState(IllegalStateException ex, HttpServletRequest request) {
        log.warn("Illegal state: path={} correlationId={}", request.getRequestURI(), correlationId());
        return build(HttpStatus.CONFLICT, "Illegal state", request);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiErrorDto> notReadable(HttpMessageNotReadableException ex, HttpServletRequest request) {
        log.warn("Invalid request body: path={} correlationId={}", request.getRequestURI(), correlationId());
        return build(HttpStatus.BAD_REQUEST, "Invalid request body", request);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorDto> unexpected(Exception ex, HttpServletRequest request) {
        log.error("Unexpected exception: type={} path={} correlationId={}",
                ex.getClass().getSimpleName(), request.getRequestURI(), correlationId(), ex);
        return build(HttpStatus.INTERNAL_SERVER_ERROR, "Internal server error", request);
    }

    private ResponseEntity<ApiErrorDto> build(HttpStatus status, String message, HttpServletRequest request) {
        ApiErrorDto body = apiErrorFactory.of(status, message, request.getRequestURI());
        return ResponseEntity.status(status).body(body);
    }

    private String correlationId() {
        return MDC.get("correlationId");
    }

    private String fmt(FieldError fe) {
        return fe.getField() + ": " + fe.getDefaultMessage();
    }
}
