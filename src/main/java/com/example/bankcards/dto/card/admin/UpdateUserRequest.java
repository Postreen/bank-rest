package com.example.bankcards.dto.card.admin;

import com.example.bankcards.entity.enums.Role;

public record UpdateUserRequest(
        Boolean enabled,
        Role role
) {}
