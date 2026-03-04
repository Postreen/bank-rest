package com.example.bankcards.dto.transfer;

import java.math.BigDecimal;

public record TransferResponse(
        Long fromCardId,
        Long toCardId,
        BigDecimal amount,
        BigDecimal fromBalance,
        BigDecimal toBalance
) {}
