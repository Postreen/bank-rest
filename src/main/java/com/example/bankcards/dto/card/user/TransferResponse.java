package com.example.bankcards.dto.card.user;

import java.math.BigDecimal;

public record TransferResponse(
        Long fromCardId,
        Long toCardId,
        BigDecimal amount,
        BigDecimal fromBalance,
        BigDecimal toBalance
) {}
