package com.example.bankcards.mapper;

import com.example.bankcards.dto.card.admin.AdminUserResponse;
import com.example.bankcards.dto.card.admin.CreateUserRequest;
import com.example.bankcards.dto.card.admin.UpdateUserRequest;
import com.example.bankcards.entity.UserEntity;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    public UserEntity fromCreateRequest(CreateUserRequest req, String passwordHash) {
        if (req == null) return null;

        UserEntity u = new UserEntity();
        u.setUsername(req.username().trim());
        u.setPasswordHash(passwordHash);
        u.setRole(req.role());
        u.setEnabled(true);
        return u;
    }

    public AdminUserResponse toAdminResponse(UserEntity entity) {
        if (entity == null) return null;

        return new AdminUserResponse(
                entity.getId(),
                entity.getUsername(),
                entity.getRole().name(),
                entity.isEnabled()
        );
    }

    public void applyUpdate(UserEntity u, UpdateUserRequest req) {
        if (u == null || req == null) return;

        if (req.enabled() != null) u.setEnabled(req.enabled());
        if (req.role() != null) u.setRole(req.role());
    }
}