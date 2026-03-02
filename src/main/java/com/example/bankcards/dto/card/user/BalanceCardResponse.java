package com.example.bankcards.dto.card.user;

import java.math.BigDecimal;

public record BalanceCardResponse(
        BigDecimal balance
) {
}
