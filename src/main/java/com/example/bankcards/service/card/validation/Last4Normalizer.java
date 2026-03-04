package com.example.bankcards.service.card.validation;

import com.example.bankcards.exception.domain.card.InvalidLast4Exception;
import org.springframework.stereotype.Component;

@Component
public final class Last4Normalizer {

    public static String normalize(String last4) {
        if (last4 == null) return null;

        String v = last4.trim();
        if (v.isEmpty()) return null;

        if (!v.matches("\\d{4}")) {
            throw new InvalidLast4Exception();
        }

        return v;
    }
}
