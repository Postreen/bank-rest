package com.example.bankcards.security.handlers;

import com.example.bankcards.exception.ApiErrorDto;
import com.example.bankcards.exception.ApiErrorFactory;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class RestAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final ObjectMapper objectMapper;
    private final ApiErrorFactory apiErrorFactory;

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException ex)
            throws IOException {

        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);

        ApiErrorDto apiErrorDto = apiErrorFactory.unauthorized(
                "Authentication required",
                request.getRequestURI()
        );

        objectMapper.writeValue(response.getOutputStream(), apiErrorDto);
    }
}
