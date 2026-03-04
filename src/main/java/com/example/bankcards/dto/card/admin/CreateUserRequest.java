package com.example.bankcards.dto.card.admin;

import com.example.bankcards.entity.enums.Role;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CreateUserRequest(

        @NotBlank
        String username,

        @NotBlank
        @Size(min = 6, max = 100)
        String password,

        @NotNull
        Role role
) {}
