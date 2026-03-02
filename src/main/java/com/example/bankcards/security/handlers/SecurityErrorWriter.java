package com.example.bankcards.security.handlers;

import com.example.bankcards.exception.api.ApiErrorDto;
import com.example.bankcards.exception.api.ApiErrorFactory;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class SecurityErrorWriter {

    private final ObjectMapper objectMapper;
    private final ApiErrorFactory apiErrorFactory;

    public void write(HttpServletResponse response, HttpStatus status, String message, String path) throws IOException {
        response.setStatus(status.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);

        ApiErrorDto body = apiErrorFactory.of(status, message, path);
        objectMapper.writeValue(response.getOutputStream(), body);
    }
}
