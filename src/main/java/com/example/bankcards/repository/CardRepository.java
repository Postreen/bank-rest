package com.example.bankcards.repository;

import com.example.bankcards.entity.CardEntity;
import com.example.bankcards.entity.enums.CardStatus;
import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface CardRepository extends JpaRepository<CardEntity, Long> {

    @Query("""
            select c from CardEntity c
            where c.owner.id = :ownerId
              and c.status <> :deleted
              and (:status is null or c.status = :status)
              and (:last4 is null or c.panLast4 = :last4)
            """)
    Page<CardEntity> searchMyCards(
            @Param("ownerId") long ownerId,
            @Param("status") CardStatus status,
            @Param("last4") String last4,
            @Param("deleted") CardStatus deleted,
            Pageable pageable
    );

    @Modifying
    @Query("""
    update CardEntity c
    set c.status = :deleted
    where c.owner.id = :ownerId
      and c.status <> :deleted
""")
    void markAllCardsDeletedByOwnerId(long ownerId, CardStatus deleted);

    Optional<CardEntity> findByIdAndOwnerId(long cardId, long ownerId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<CardEntity> findForUpdateByIdAndOwnerIdAndStatusNot(
            long id,
            long ownerId,
            CardStatus status
    );

    boolean existsByPanHash(String panHash);
}