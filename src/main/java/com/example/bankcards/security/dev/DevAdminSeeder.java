package com.example.bankcards.security.dev;

import com.example.bankcards.entity.UserEntity;
import com.example.bankcards.entity.enums.Role;
import com.example.bankcards.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@Profile("dev")
@EnableConfigurationProperties(DevAdminProperties.class)
@RequiredArgsConstructor
public class DevAdminSeeder implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final DevAdminProperties props;

    @Override
    public void run(String... args) {
        if (!props.enabled()) {
            return;
        }

        String username = props.username();
        String rawPassword = props.password();

        if (userRepository.existsByUsername(username)) {
            return;
        }

        UserEntity admin = new UserEntity();
        admin.setUsername(username);
        admin.setPasswordHash(passwordEncoder.encode(rawPassword));
        admin.setRole(Role.ADMIN);
        admin.setEnabled(true);

        userRepository.save(admin);
    }
}