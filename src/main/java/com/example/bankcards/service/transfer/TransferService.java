package com.example.bankcards.service.transfer;

import com.example.bankcards.dto.transfer.TransferRequest;
import com.example.bankcards.dto.transfer.TransferResponse;
import com.example.bankcards.entity.CardEntity;
import com.example.bankcards.service.card.CardService;
import com.example.bankcards.service.transfer.validation.TransferValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class TransferService {

    private final TransferValidator validator;
    private final CardService cardService;
    private final TransferDomainService domain;

    @Transactional
    public TransferResponse transfer(long ownerId, TransferRequest req) {
        validator.validate(req);

        String transferId = UUID.randomUUID().toString();
        BigDecimal amount = req.amount();
        log.info("Transfer started: transferId={} ownerId={} fromCardId={} toCardId={}",
                transferId, ownerId, req.fromCardId(), req.toCardId());

        CardEntity first = cardService.loadForUpdate(req.fromCardId(), ownerId);
        CardEntity second = cardService.loadForUpdate(req.toCardId(), ownerId);

        CardEntity from = (req.fromCardId() == first.getId()) ? first : second;
        CardEntity to = (req.toCardId() == first.getId()) ? first : second;

        domain.execute(from, to, amount);

        log.info("Transfer completed: transferId={} ownerId={} fromCardId={} toCardId={}",
                transferId, ownerId, from.getId(), to.getId());
        return new TransferResponse(
                from.getId(),
                to.getId(),
                amount,
                from.getBalance(),
                to.getBalance()
        );
    }
}