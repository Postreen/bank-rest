package com.example.bankcards.service.user;

import com.example.bankcards.dto.card.admin.AdminUserResponse;
import com.example.bankcards.dto.card.admin.CreateUserRequest;
import com.example.bankcards.dto.card.admin.UpdateUserRequest;
import com.example.bankcards.entity.UserEntity;
import com.example.bankcards.entity.enums.CardStatus;
import com.example.bankcards.exception.domain.user.UserNotFoundException;
import com.example.bankcards.exception.domain.user.UsernameAlreadyExistsException;
import com.example.bankcards.mapper.UserMapper;
import com.example.bankcards.repository.CardRepository;
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

    private final CardRepository cardRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;


    @Transactional(readOnly = true)
    public Page<AdminUserResponse> getUsers(Pageable pageable) {
        return userRepository.findAllByEnabledTrue(pageable)
                .map(userMapper::toAdminResponse);
    }

    @Transactional
    public AdminUserResponse createUser(CreateUserRequest createUserRequest) {
        String username = createUserRequest.username().trim();

        if (userRepository.existsByUsername(username)) {
            throw new UsernameAlreadyExistsException(username);
        }

        String hash = passwordEncoder.encode(createUserRequest.password());

        UserEntity userEntity = userMapper.fromCreateRequest(createUserRequest, hash);
        userEntity.setUsername(username);

        UserEntity saved = userRepository.save(userEntity);
        return userMapper.toAdminResponse(saved);
    }

    @Transactional
    public AdminUserResponse updateUser(long id, UpdateUserRequest updateUserRequest) {
        UserEntity userEntity = userRepository.findById(id)
                .orElseThrow(UserNotFoundException::new);

        userMapper.applyUpdate(userEntity, updateUserRequest);

        return userMapper.toAdminResponse(userEntity);
    }

    @Transactional
    public void deleteUser(long id) {
        UserEntity userEntity = userRepository.findById(id)
                .orElseThrow(UserNotFoundException::new);

        if (userEntity.isEnabled()) {
            userEntity.setEnabled(false);
        }

        cardRepository.markAllCardsDeletedByOwnerId(id, CardStatus.DELETED);
    }
}