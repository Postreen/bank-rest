package com.example.bankcards.service.user;

import com.example.bankcards.dto.card.admin.AdminUserResponse;
import com.example.bankcards.dto.card.admin.CreateUserRequest;
import com.example.bankcards.dto.card.admin.UpdateUserRequest;
import com.example.bankcards.entity.UserEntity;
import com.example.bankcards.exception.domain.user.UserNotFoundException;
import com.example.bankcards.exception.domain.user.UsernameAlreadyExistsException;
import com.example.bankcards.mapper.UserMapper;
import com.example.bankcards.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AdminUserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;

    public Page<AdminUserResponse> getUsers(Pageable pageable) {
        return userRepository.findAll(pageable)
                .map(userMapper::toAdminResponse);
    }

    @Transactional
    public AdminUserResponse createUser(CreateUserRequest createUserRequest) {
        String username = createUserRequest.username().trim();

        if (userRepository.existsByUsername(username)) {
            throw new UsernameAlreadyExistsException(username);
        }

        String hash = passwordEncoder.encode(createUserRequest.password());

        UserEntity u = userMapper.fromCreateRequest(createUserRequest, hash);
        u.setUsername(username);

        UserEntity saved = userRepository.save(u);
        return userMapper.toAdminResponse(saved);
    }

    @Transactional
    public AdminUserResponse updateUser(long id, UpdateUserRequest req) {
        UserEntity u = userRepository.findById(id)
                .orElseThrow(UserNotFoundException::new);

        userMapper.applyUpdate(u, req);

        return userMapper.toAdminResponse(u);
    }

    @Transactional
    public void deleteUser(long id) {
        if (!userRepository.existsById(id)) {
            throw new UserNotFoundException();
        }
        userRepository.deleteById(id);
    }
}