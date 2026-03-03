package com.example.bankcards.mapper;

import com.example.bankcards.dto.card.admin.AdminUserResponse;
import com.example.bankcards.entity.UserEntity;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    public AdminUserResponse toAdminResponse(UserEntity entity) {
        if (entity == null) return null;

        return new AdminUserResponse(
                entity.getId(),
                entity.getUsername(),
                entity.getRole().name(),
                entity.isEnabled()
        );
    }
}