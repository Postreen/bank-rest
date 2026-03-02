package com.example.bankcards.service.auth;

import com.example.bankcards.dto.auth.LoginResult;
import com.example.bankcards.entity.UserEntity;
import com.example.bankcards.exception.InvalidCredentialsException;
import com.example.bankcards.repository.UserRepository;
import com.example.bankcards.security.jwt.JwtProperties;
import com.example.bankcards.security.jwt.JwtTokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenService tokenService;
    private final JwtProperties jwtProperties;

    public LoginResult login(String username, String password) {
        UserEntity user = userRepository.findByUsername(username).orElse(null);

        if (user == null || !user.isEnabled() || !passwordEncoder.matches(password, user.getPasswordHash())) {
            throw new InvalidCredentialsException();
        }

        String token = tokenService.generateToken(user.getId(), user.getUsername(), user.getRole());
        return new LoginResult(token, jwtProperties.ttlSeconds());
    }
}
