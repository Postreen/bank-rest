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
        String validPan = validatePan(pan);

        String encrypted = crypto.encryptToBase64(validPan);
        String hash = crypto.hash(validPan);
        String last4 = validPan.substring(validPan.length() - 4);

        return new ProtectedPan(encrypted, hash, last4);
    }

    private String validatePan(String pan) {
        if (pan == null) {
            throw new InvalidPanException();
        }
        String validPan = pan.replace(" ", "").trim();
        if (validPan.length() < 4) {
            throw new InvalidPanException();
        }
        return validPan;
    }
}
