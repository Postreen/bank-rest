package com.example.bankcards.service.admin;

import com.example.bankcards.dto.card.admin.AdminUserResponse;
import com.example.bankcards.dto.card.admin.CreateUserRequest;
import com.example.bankcards.dto.card.admin.UpdateUserRequest;
import com.example.bankcards.entity.UserEntity;
import com.example.bankcards.mapper.UserMapper;
import com.example.bankcards.service.card.CardService;
import com.example.bankcards.service.user.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class AdminUserService {

    private final CardService cardService;
    private final UserService userService;
    private final UserMapper userMapper;

    @Transactional(readOnly = true)
    public Page<AdminUserResponse> getUsers(Pageable pageable) {
        return userService.getEnabledUsers(pageable)
                .map(userMapper::toAdminResponse);
    }

    @Transactional
    public AdminUserResponse createUser(CreateUserRequest createUserRequest) {
        UserEntity user = userService.createUserOrThrow(
                createUserRequest.username(),
                createUserRequest.password(),
                createUserRequest.role()
        );
        log.info("User created: userId={} username={} role={}", user.getId(), user.getUsername(), user.getRole());
        return userMapper.toAdminResponse(user);
    }

    @Transactional
    public AdminUserResponse updateUser(long userId, UpdateUserRequest updateUserRequest) {
        UserEntity user = userService.updateUser(
                userId,
                updateUserRequest.enabled(),
                updateUserRequest.role()
        );
        log.info("User updated: userId={} enabled={} role={}", user.getId(), user.isEnabled(), user.getRole());
        return userMapper.toAdminResponse(user);
    }

    @Transactional
    public void deleteUserById(long userId) {
        cardService.deleteAllCardsByOwnerId(userId);
        userService.deleteUserById(userId);
        log.info("User deleted: userId={}", userId);
    }

}