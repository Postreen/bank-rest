package com.example.bankcards.security.crypto;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Base64;

@Service
@RequiredArgsConstructor
public class CardCryptoService {

    private static final String ALGO = "AES";
    private static final String TRANSFORMATION = "AES/GCM/NoPadding";
    private static final int IV_LEN = 12;
    private static final int TAG_LEN_BITS = 128;

    private final CardCryptoProperties props;
    private final SecureRandom random = new SecureRandom();

    private SecretKey key;

    @PostConstruct
    public void init() {
        if (props.key() == null || props.key().isBlank()) {
            throw new IllegalStateException("CARD_CRYPTO_KEY is not set");
        }

        byte[] raw = props.key().getBytes(StandardCharsets.UTF_8);
        if (raw.length != 32) {
            throw new IllegalStateException("CARD_CRYPTO_KEY must be exactly 32 bytes (UTF-8 length). Current=" + raw.length);
        }
        this.key = new SecretKeySpec(raw, ALGO);
    }

    public String encryptToBase64(String plaintext) {
        try {
            byte[] iv = new byte[IV_LEN];
            random.nextBytes(iv);

            Cipher cipher = Cipher.getInstance(TRANSFORMATION);
            cipher.init(Cipher.ENCRYPT_MODE, key, new GCMParameterSpec(TAG_LEN_BITS, iv));

            byte[] ct = cipher.doFinal(plaintext.getBytes(StandardCharsets.UTF_8));

            byte[] out = new byte[iv.length + ct.length];
            System.arraycopy(iv, 0, out, 0, iv.length);
            System.arraycopy(ct, 0, out, iv.length, ct.length);

            return Base64.getEncoder().encodeToString(out);
        } catch (Exception e) {
            throw new IllegalStateException("PAN encryption failed", e);
        }
    }

    public String decryptFromBase64(String encoded) {
        try {
            byte[] in = Base64.getDecoder().decode(encoded);
            if (in.length <= IV_LEN) throw new IllegalArgumentException("Invalid encrypted payload");

            byte[] iv = new byte[IV_LEN];
            byte[] ct = new byte[in.length - IV_LEN];

            System.arraycopy(in, 0, iv, 0, IV_LEN);
            System.arraycopy(in, IV_LEN, ct, 0, ct.length);

            Cipher cipher = Cipher.getInstance(TRANSFORMATION);
            cipher.init(Cipher.DECRYPT_MODE, key, new GCMParameterSpec(TAG_LEN_BITS, iv));

            byte[] pt = cipher.doFinal(ct);
            return new String(pt, StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new IllegalArgumentException("PAN decryption failed", e);
        }
    }

    public String hash(String pan) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(pan.getBytes(StandardCharsets.UTF_8));
            return toHex(hash);
        } catch (Exception e) {
            throw new IllegalStateException("PAN hash failed", e);
        }
    }

    private static String toHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder(bytes.length * 2);
        for (byte b : bytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }
}
