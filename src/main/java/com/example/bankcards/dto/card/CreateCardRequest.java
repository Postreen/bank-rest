package com.example.bankcards.dto.card;

import jakarta.validation.constraints.*;

public record CreateCardRequest(
        @NotNull
        Long ownerId,

        @NotBlank
        @Pattern(regexp = "\\d{16}", message = "PAN must be 16 digits")
        String pan,

        @NotBlank
        String holderName,

        @Min(1) @Max(12)
        int expiryMonth,

        @Min(2024)
        int expiryYear
) {
}
