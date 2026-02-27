package com.example.bankcards.security.dev;

import com.example.bankcards.repository.UserRepository;
import com.example.bankcards.security.jwt.JwtTokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile("dev")
@RequiredArgsConstructor
public class DevJwtPrinter implements CommandLineRunner {

    private final UserRepository userRepository;
    private final JwtTokenService tokenService;

    @Override
    public void run(String... args) {
        userRepository.findByUsername("admin").ifPresent(admin -> {
            String token = tokenService.generateToken(admin.getId(), admin.getUsername(), admin.getRole());
            System.out.println("[DEV] Admin JWT (Bearer): " + token);
        });
    }
}
