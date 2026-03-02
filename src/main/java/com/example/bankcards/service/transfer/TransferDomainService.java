package com.example.bankcards.service.transfer;

import com.example.bankcards.entity.CardEntity;
import com.example.bankcards.entity.enums.CardStatus;
import com.example.bankcards.exception.domain.card.CardNotActiveException;
import com.example.bankcards.exception.domain.card.InsufficientFundsException;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class TransferDomainService {

    public void execute(CardEntity from, CardEntity to, BigDecimal amount) {
        ensureActive(from);
        ensureActive(to);
        ensureSufficientFunds(from, amount);

        from.setBalance(from.getBalance().subtract(amount));
        to.setBalance(to.getBalance().add(amount));
    }

    private void ensureActive(CardEntity c) {
        if (c.getStatus() != CardStatus.ACTIVE) {
            throw new CardNotActiveException();
        }
    }

    private void ensureSufficientFunds(CardEntity from, BigDecimal amount) {
        if (from.getBalance().compareTo(amount) < 0) {
            throw new InsufficientFundsException();
        }
    }
}
