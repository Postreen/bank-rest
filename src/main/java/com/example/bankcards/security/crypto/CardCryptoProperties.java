package com.example.bankcards.security.crypto;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "security.card-crypto")
public record CardCryptoProperties(
        String key
) {}
