package bankcards.security.crypto;


import com.example.bankcards.security.crypto.CardCryptoService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

@ActiveProfiles("test")
@SpringBootTest(classes = com.example.bankcards.BankRestApplication.class)
class CardCryptoServiceTest {

    @Autowired
    CardCryptoService svc;

    @Test
    void encrypt_decrypt_roundtrip() {
        String pan = "4111111111111111";
        String enc = svc.encryptToBase64(pan);

        assertNotEquals(pan, enc);
        assertEquals(pan, svc.decryptFromBase64(enc));
    }
}
