package com.example.bankcards.security.handlers;

import com.example.bankcards.exception.ApiErrorDto;
import com.example.bankcards.exception.ApiErrorFactory;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class RestAccessDeniedHandler implements AccessDeniedHandler {

    private final ObjectMapper objectMapper;
    private final ApiErrorFactory apiErrorFactory;

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException ex)
            throws IOException {

        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);

        ApiErrorDto apiErrorDto = apiErrorFactory.forbidden(
                "Access denied",
                request.getRequestURI()
        );

        objectMapper.writeValue(response.getOutputStream(), apiErrorDto);
    }
}