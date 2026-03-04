package com.example.bankcards.repository;

import com.example.bankcards.entity.CardEntity;
import com.example.bankcards.entity.enums.CardStatus;
import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface CardRepository extends JpaRepository<CardEntity, Long> {

    @Query("""
            select c from CardEntity c
            where c.owner.id = :ownerId
              and c.status <> :blocked
              and (:status is null or c.status = :status)
              and (:last4 is null or c.panLast4 = :last4)
            """)
    Page<CardEntity> searchOwnedCards(
            @Param("ownerId") long ownerId,
            @Param("status") CardStatus status,
            @Param("last4") String last4,
            @Param("blocked") CardStatus blocked,
            Pageable pageable
    );

    Optional<CardEntity> findByIdAndOwnerId(long id, long ownerId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<CardEntity> findWithLockByIdAndOwnerId(long id, long ownerId);

    void deleteAllByOwnerId(Long ownerId);

    boolean existsByPanHash(String panHash);
}