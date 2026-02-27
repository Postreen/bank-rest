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
@EnableConfigurationProperties(DevUsersProperties.class)
@RequiredArgsConstructor
public class DevUsersSeeder implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final DevUsersProperties props;

    @Override
    public void run(String... args) {
        if (!props.enabled()) return;

        createIfMissing(props.adminUsername(), props.adminPassword(), Role.ADMIN);
        createIfMissing(props.userUsername(), props.userPassword(), Role.USER);
    }

    private void createIfMissing(String username, String rawPassword, Role role) {
        if (userRepository.existsByUsername(username)) return;

        UserEntity u = new UserEntity();
        u.setUsername(username);
        u.setPasswordHash(passwordEncoder.encode(rawPassword));
        u.setRole(role);
        u.setEnabled(true);

        userRepository.save(u);
    }
}