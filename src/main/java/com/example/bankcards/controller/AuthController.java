package com.example.bankcards.controller;

import com.example.bankcards.dto.auth.LoginRequest;
import com.example.bankcards.dto.auth.LoginResponse;
import com.example.bankcards.dto.auth.MeResponse;
import com.example.bankcards.exception.ApiErrorDto;
import com.example.bankcards.exception.ApiErrorFactory;
import com.example.bankcards.security.jwt.JwtPrincipal;
import com.example.bankcards.service.auth.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final ApiErrorFactory apiErrorFactory;

    @PostMapping("/login")
    public LoginResponse login(@Valid @RequestBody LoginRequest request) {
        var result = authService.login(request.username(), request.password());
        return new LoginResponse(result.token(), "Bearer", result.expiresIn());
    }

    @GetMapping("/me")
    public MeResponse me(Authentication authentication) {
        JwtPrincipal principal = (JwtPrincipal) authentication.getPrincipal();
        return new MeResponse(principal.userId(), principal.username(), principal.role().name());
    }

    @ExceptionHandler(AuthService.InvalidCredentialsException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ApiErrorDto invalidCredentials(HttpServletRequest request) {
        return apiErrorFactory.unauthorized("Invalid credentials", request.getRequestURI());
    }
}
