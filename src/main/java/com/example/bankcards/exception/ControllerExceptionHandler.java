package com.example.bankcards.exception;

import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.stream.Collectors;

@RestControllerAdvice
@RequiredArgsConstructor
public class ControllerExceptionHandler {
    private final ApiErrorFactory apiErrorFactory;

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiErrorDto validation(MethodArgumentNotValidException ex, HttpServletRequest request) {
        String msg = ex.getBindingResult().getFieldErrors().stream()
                .map(this::fmt)
                .collect(Collectors.joining("; "));
        return apiErrorFactory.badRequest(msg, request.getRequestURI());
    }

    @ExceptionHandler(InvalidCredentialsException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ApiErrorDto invalidCredentials(InvalidCredentialsException ex, HttpServletRequest request) {
        String msg = (ex.getMessage() == null || ex.getMessage().isBlank())
                ? "Invalid credentials"
                : ex.getMessage();
        return apiErrorFactory.unauthorized(msg, request.getRequestURI());
    }

    @ExceptionHandler(CardOperationException.class)
    public ResponseEntity<ApiErrorDto> cardOperation(CardOperationException ex, HttpServletRequest request) {
        ApiErrorDto body = apiErrorFactory.of(ex.getStatus(), ex.getMessage(), request.getRequestURI());
        return ResponseEntity.status(ex.getStatus()).body(body);
    }

    @ExceptionHandler(EntityNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ApiErrorDto notFound(EntityNotFoundException ex, HttpServletRequest request) {
        return apiErrorFactory.of(HttpStatus.NOT_FOUND, ex.getMessage(), request.getRequestURI());
    }

    private String fmt(FieldError fe) {
        return fe.getField() + ": " + fe.getDefaultMessage();
    }

}
