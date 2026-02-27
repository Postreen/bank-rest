package com.example.bankcards.repository;

import com.example.bankcards.entity.CardEntity;
import com.example.bankcards.entity.UserEntity;
import com.example.bankcards.entity.enums.CardStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CardRepository extends JpaRepository<CardEntity, Long> {

    Page<CardEntity> findByOwner(UserEntity owner, Pageable pageable);

    Page<CardEntity> findByOwnerId(long ownerId, Pageable pageable);

    Page<CardEntity> findByOwnerIdAndStatus(long ownerId, CardStatus status, Pageable pageable);

    Page<CardEntity> findByOwnerIdAndPanLast4Containing(long ownerId, String last4Part, Pageable pageable);

    Page<CardEntity> findByOwnerIdAndStatusAndPanLast4Containing(
            long ownerId,
            CardStatus status,
            String last4Part,
            Pageable pageable
    );
}
