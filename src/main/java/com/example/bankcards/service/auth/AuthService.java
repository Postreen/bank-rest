package com.example.bankcards.service.auth;

import com.example.bankcards.dto.auth.LoginResult;
import com.example.bankcards.entity.UserEntity;
import com.example.bankcards.exception.domain.auth.InvalidCredentialsException;
import com.example.bankcards.repository.UserRepository;
import com.example.bankcards.security.jwt.JwtProperties;
import com.example.bankcards.security.jwt.JwtTokenService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenService tokenService;
    private final JwtProperties jwtProperties;

    public LoginResult login(String username, String password) {
        String u = normalize(username);
        String p = normalize(password);

        if (u.isEmpty() || p.isEmpty()) {
            log.warn("Login rejected: empty credentials provided");
            throw new InvalidCredentialsException();
        }

        UserEntity user = userRepository.findByUsername(u)
                .filter(UserEntity::isEnabled)
                .filter(found -> passwordEncoder.matches(p, found.getPasswordHash()))
                .orElseThrow(() -> {
                    log.warn("Login rejected: invalid credentials for username={}", u);
                    return new InvalidCredentialsException();
                });

        String token = tokenService.generateToken(user.getId(), user.getUsername(), user.getRole());
        log.info("Login success: userId={} username={}", user.getId(), user.getUsername());
        return new LoginResult(token, jwtProperties.ttlSeconds());
    }

    private static String normalize(String s) {
        return s == null ? "" : s.trim();
    }
}
