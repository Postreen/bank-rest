package bankcards.service.card;

import com.example.bankcards.entity.CardEntity;
import com.example.bankcards.entity.enums.CardStatus;
import com.example.bankcards.service.card.CardService;
import com.example.bankcards.service.card.factory.CardFactory;
import com.example.bankcards.service.card.pan.CardPanService;
import com.example.bankcards.repository.CardRepository;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ActiveProfiles("test")
@SpringBootTest(classes = com.example.bankcards.BankRestApplication.class)
class CardServiceExpiryTest {

    private final CardRepository cardRepository = org.mockito.Mockito.mock(CardRepository.class);
    private final CardPanService cardPanService = org.mockito.Mockito.mock(CardPanService.class);
    private final CardFactory cardFactory = org.mockito.Mockito.mock(CardFactory.class);
    private final Clock fixedClock = Clock.fixed(Instant.parse("2026-03-01T00:00:00Z"), ZoneOffset.UTC);

    private final CardService cardService = new CardService(cardRepository, cardPanService, cardFactory, fixedClock);

    @Test
    void applyExpiryIfNeeded_marks_expired_when_date_in_past() {
        CardEntity card = new CardEntity();
        card.setStatus(CardStatus.ACTIVE);
        card.setExpiryMonth(2);
        card.setExpiryYear(2026);

        cardService.applyExpiryIfNeeded(card);

        assertEquals(CardStatus.EXPIRED, card.getStatus());
    }

    @Test
    void applyExpiryIfNeeded_keeps_active_when_expiry_current_month() {
        CardEntity card = new CardEntity();
        card.setStatus(CardStatus.ACTIVE);
        card.setExpiryMonth(3);
        card.setExpiryYear(2026);

        cardService.applyExpiryIfNeeded(card);

        assertEquals(CardStatus.ACTIVE, card.getStatus());
    }
}
