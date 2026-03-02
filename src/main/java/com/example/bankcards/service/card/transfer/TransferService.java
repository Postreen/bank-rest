package com.example.bankcards.service.card.transfer;

import com.example.bankcards.dto.card.user.TransferRequest;
import com.example.bankcards.dto.card.user.TransferResponse;
import com.example.bankcards.entity.CardEntity;
import com.example.bankcards.entity.enums.CardStatus;
import com.example.bankcards.exception.domain.card.CardNotActiveException;
import com.example.bankcards.exception.domain.card.CardNotFoundException;
import com.example.bankcards.exception.domain.card.InsufficientFundsException;
import com.example.bankcards.repository.CardRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class TransferService {

    private final CardRepository cardRepository;

    @Transactional
    public TransferResponse transfer(long ownerId, TransferRequest transferRequest) {
        if (transferRequest.fromCardId().equals(transferRequest.toCardId())) {
            throw new IllegalArgumentException("fromCardId and toCardId must be different");
        }

        BigDecimal amount = transferRequest.amount();
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Amount must be > 0");
        }

        long firstId = Math.min(transferRequest.fromCardId(), transferRequest.toCardId());
        long secondId = Math.max(transferRequest.fromCardId(), transferRequest.toCardId());

        CardEntity first = getMyCardForUpdate(ownerId, firstId);
        CardEntity second = getMyCardForUpdate(ownerId, secondId);

        CardEntity from = transferRequest.fromCardId().equals(first.getId()) ? first : second;
        CardEntity to = transferRequest.toCardId().equals(first.getId()) ? first : second;

        ensureActive(from);
        ensureActive(to);

        if (from.getBalance().compareTo(amount) < 0) {
            throw new InsufficientFundsException();
        }

        from.setBalance(from.getBalance().subtract(amount));
        to.setBalance(to.getBalance().add(amount));

        return new TransferResponse(
                from.getId(),
                to.getId(),
                amount,
                from.getBalance(),
                to.getBalance()
        );
    }

    private CardEntity getMyCardForUpdate(long ownerId, long cardId) {
        return cardRepository.findMyCardForUpdate(ownerId, cardId, CardStatus.DELETED)
                .orElseThrow(CardNotFoundException::new);
    }

    private void ensureActive(CardEntity c) {
        if (c.getStatus() != CardStatus.ACTIVE) {
            throw new CardNotActiveException();
        }
    }
}
