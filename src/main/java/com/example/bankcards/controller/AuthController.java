package com.example.bankcards.controller;

import com.example.bankcards.dto.auth.LoginRequest;
import com.example.bankcards.dto.auth.LoginResponse;
import com.example.bankcards.dto.auth.LoginResult;
import com.example.bankcards.dto.auth.MeResponse;
import com.example.bankcards.security.jwt.JwtPrincipal;
import com.example.bankcards.service.auth.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public LoginResponse login(@Valid @RequestBody LoginRequest request) {
        LoginResult result = authService.login(request.username(), request.password());
        return new LoginResponse(result.token(), "Bearer", result.expiresIn());
    }

    @GetMapping("/me")
    @PreAuthorize("isAuthenticated()")
    public MeResponse me(@AuthenticationPrincipal JwtPrincipal principal) {
        return new MeResponse(principal.userId(), principal.username(), principal.role().name());
    }

}
