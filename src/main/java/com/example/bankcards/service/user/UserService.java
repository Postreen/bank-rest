package com.example.bankcards.service.user;

import com.example.bankcards.entity.UserEntity;
import com.example.bankcards.entity.enums.Role;
import com.example.bankcards.exception.domain.user.UserNotFoundException;
import com.example.bankcards.exception.domain.user.UsernameAlreadyExistsException;
import com.example.bankcards.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional(readOnly = true)
    public Page<UserEntity> getEnabledUsers(Pageable pageable) {
        return userRepository.findAllByEnabledTrue(pageable);
    }
    @Transactional(readOnly = true)
    public UserEntity getUserOrThrow(long userId) {
        return userRepository.findById(userId).orElseThrow(UserNotFoundException::new);
    }

    @Transactional
    public UserEntity createUserOrThrow(String userNameRaw, String passwordRaw, Role role) {
        String username = normalizeUsername(userNameRaw);

        if (userRepository.existsByUsername(username)) {
            throw new UsernameAlreadyExistsException(username);
        }

        String hash = passwordEncoder.encode(passwordRaw);

        UserEntity user = new UserEntity();
        user.setUsername(username);
        user.setPasswordHash(hash);
        user.setRole(role);
        user.setEnabled(true);

        return userRepository.save(user);
    }

    @Transactional
    public UserEntity updateUser(long userId, Boolean enabled, Role role) {
        UserEntity user = getUserOrThrow(userId);

        if (enabled != null) user.setEnabled(enabled);
        if (role != null) user.setRole(role);

        return user;
    }

    @Transactional
    public void deleteUserById(long userId) {
        UserEntity user = getUserOrThrow(userId);
        userRepository.delete(user);
    }

    private String normalizeUsername(String usernameRaw) {
        if (usernameRaw == null) return "";
        return usernameRaw.trim();
    }
}
