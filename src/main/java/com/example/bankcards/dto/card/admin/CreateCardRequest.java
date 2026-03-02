package com.example.bankcards.dto.card.admin;

import com.example.bankcards.dto.card.admin.validation.ValidPan;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateCardRequest(
        @NotNull
        Long ownerId,

        @NotBlank
        @ValidPan
        String pan,

        @NotBlank
        String holderName,

        @Min(1) @Max(12)
        int expiryMonth,

        @Min(2024)
        int expiryYear
) {
}
