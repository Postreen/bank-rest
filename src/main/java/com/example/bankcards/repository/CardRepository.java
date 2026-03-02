package com.example.bankcards.repository;

import com.example.bankcards.entity.CardEntity;
import com.example.bankcards.entity.UserEntity;
import com.example.bankcards.entity.enums.CardStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

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

    @Query("""
            select c from CardEntity c
            where c.owner.id = :ownerId
              and c.status <> CardStatus.DELETED
            """)
    Page<CardEntity> findMyCards(@Param("ownerId") long ownerId, Pageable pageable);

    @Query("""
            select c from CardEntity c
            where c.owner.id = :ownerId
              and c.status <> CardStatus.DELETED
              and (:status is null or c.status = :status)
              and (:last4 is null or c.panLast4 like concat('%', :last4, '%'))
            """)
    Page<CardEntity> searchMyCards(
            @Param("ownerId") long ownerId,
            @Param("status") CardStatus status,
            @Param("last4") String last4,
            Pageable pageable
    );

    @Query("""
            select c from CardEntity c
            where c.id = :cardId
              and c.owner.id = :ownerId
              and c.status <> CardStatus.DELETED
            """)
    Optional<CardEntity> findMyCardById(
            @Param("ownerId") long ownerId,
            @Param("cardId") long cardId
    );
}
