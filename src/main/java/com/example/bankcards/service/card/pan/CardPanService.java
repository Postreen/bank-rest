package com.example.bankcards.service.card.pan;

import com.example.bankcards.security.crypto.CardCryptoService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CardPanService {
    private final CardCryptoService crypto;

    public ProtectedPan protect(String pan) {
        String encrypted = crypto.encryptToBase64(pan);
        String hash = crypto.hash(pan);
        String last4 = pan.substring(pan.length() - 4);
        return new ProtectedPan(encrypted, hash, last4);
    }
}
