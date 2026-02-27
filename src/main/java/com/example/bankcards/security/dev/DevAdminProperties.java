package com.example.bankcards.security.dev;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.dev-admin")
public record DevAdminProperties(
        String username,
        String password,
        boolean enabled
) {}
