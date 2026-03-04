package bankcards.service.transfer;

import com.example.bankcards.entity.CardEntity;
import com.example.bankcards.entity.enums.CardStatus;
import com.example.bankcards.exception.domain.card.CardNotActiveException;
import com.example.bankcards.exception.domain.card.InsufficientFundsException;
import com.example.bankcards.service.transfer.TransferDomainService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ActiveProfiles("test")
@SpringBootTest(classes = com.example.bankcards.BankRestApplication.class)
class TransferDomainServiceTest {

    private final TransferDomainService service = new TransferDomainService();

    @Test
    void transfer_success_changes_balances() {
        CardEntity from = card(CardStatus.ACTIVE, "100.00");
        CardEntity to = card(CardStatus.ACTIVE, "10.00");

        service.execute(from, to, new BigDecimal("25.50"));

        assertEquals(new BigDecimal("74.50"), from.getBalance());
        assertEquals(new BigDecimal("35.50"), to.getBalance());
    }

    @Test
    void transfer_fails_when_insufficient_funds() {
        CardEntity from = card(CardStatus.ACTIVE, "10.00");
        CardEntity to = card(CardStatus.ACTIVE, "5.00");

        assertThrows(InsufficientFundsException.class,
                () -> service.execute(from, to, new BigDecimal("11.00")));
    }

    @Test
    void transfer_fails_when_card_not_active() {
        CardEntity from = card(CardStatus.BLOCKED, "10.00");
        CardEntity to = card(CardStatus.ACTIVE, "5.00");

        assertThrows(CardNotActiveException.class,
                () -> service.execute(from, to, new BigDecimal("1.00")));
    }

    private CardEntity card(CardStatus status, String balance) {
        CardEntity card = new CardEntity();
        card.setStatus(status);
        card.setBalance(new BigDecimal(balance));
        return card;
    }
}
