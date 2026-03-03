package com.example.bankcards.service.card.pan;

import com.example.bankcards.exception.domain.card.InvalidPanException;
import com.example.bankcards.security.crypto.CardCryptoService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class CardPanService {

    private final CardCryptoService crypto;

    public ProtectedPan protectPan(String pan) {
        if (pan == null) {
            throw new InvalidPanException();
        }

        String normalized = pan.replace(" ", "").trim();
        if (normalized.length() < 4) {
            throw new InvalidPanException();
        }

        String encrypted = crypto.encryptToBase64(normalized);
        String hash = crypto.hash(normalized);
        String last4 = normalized.substring(normalized.length() - 4);

        return new ProtectedPan(encrypted, hash, last4);
    }
}
