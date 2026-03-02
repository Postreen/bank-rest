package com.example.bankcards.dto.card.api;

import java.math.BigDecimal;

public record BalanceResponse(
        BigDecimal balance
) {
}
