package com.example.bankcards.entity;

import com.example.bankcards.entity.enums.CardStatus;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Entity
@Table(name = "cards",
        indexes = {
                @Index(name = "idx_cards_owner_id", columnList = "owner_id"),
                @Index(name = "idx_cards_owner_status", columnList = "owner_id, status"),
                @Index(name = "idx_cards_last4", columnList = "pan_last4")
        }
)
@Getter
@Setter
@NoArgsConstructor
public class CardEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "owner_id", nullable = false)
    private UserEntity owner;

    @Column(name = "encrypted_pan", nullable = false, columnDefinition = "text")
    private String encryptedPan;

    @Column(name = "pan_hash", nullable = false, unique = true, length = 64)
    private String panHash;

    @Column(name = "pan_last4", nullable = false, length = 4)
    private String panLast4;

    @Column(name = "holder_name", nullable = false, length = 120)
    private String holderName;

    @Column(name = "expiry_month", nullable = false)
    private int expiryMonth;

    @Column(name = "expiry_year", nullable = false)
    private int expiryYear;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private CardStatus status;

    @Column(name = "balance", nullable = false, precision = 19, scale = 2)
    private BigDecimal balance;

    @Column(name = "created_at", nullable = false)
    private OffsetDateTime createdAt;
}
