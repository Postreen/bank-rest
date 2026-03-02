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
import org.springframework.transaction.annotation.Transactional;

@Component
@Profile("dev")
@EnableConfigurationProperties(DevUsersProperties.class)
@RequiredArgsConstructor
public class DevUsersSeeder implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final DevUsersProperties props;

    @Transactional
    @Override
    public void run(String... args) {
        if (!props.enabled()) return;

        upsert(props.adminUsername(), props.adminPassword(), Role.ADMIN);
        upsert(props.userUsername(), props.userPassword(), Role.USER);
    }

    private void upsert(String username, String rawPassword, Role role) {
        UserEntity u = userRepository.findByUsername(username)
                .orElseGet(UserEntity::new);

        u.setUsername(username);
        u.setPasswordHash(passwordEncoder.encode(rawPassword));
        u.setRole(role);
        u.setEnabled(true);

        userRepository.save(u);
    }
}