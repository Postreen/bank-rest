package com.example.bankcards.dto.card;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateCardRequest(
        @NotNull
        Long ownerId,

        @NotBlank
        String pan,

        @NotBlank
        String holderName,

        @Min(1) @Max(12)
        int expiryMonth,

        @Min(2024)
        int expiryYear
) {
}
