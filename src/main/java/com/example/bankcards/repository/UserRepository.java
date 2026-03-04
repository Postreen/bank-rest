package com.example.bankcards.repository;

import com.example.bankcards.entity.UserEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<UserEntity, Long> {
    Page<UserEntity> findAllByEnabledTrue(Pageable pageable);
    Optional<UserEntity> findByUsername(String username);
    boolean existsByUsername(String username);
}
