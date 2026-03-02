package com.example.bankcards.dto.card.admin;

import com.example.bankcards.entity.enums.CardStatus;
import jakarta.validation.constraints.NotNull;

public record UpdateCardStatusRequest(
        @NotNull
        CardStatus status
){
}
