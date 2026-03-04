package com.example.bankcards.security.dev;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.dev-users")
public record DevUsersProperties(
        boolean enabled,
        String adminUsername,
        String adminPassword,
        String userUsername,
        String userPassword
) {
}
