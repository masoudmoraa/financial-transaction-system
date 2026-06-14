package com.example.bank.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import com.example.bank.enums.TransactionType;
import com.example.bank.enums.TransactionDirection;

@Entity
@Table(
        name = "TRANSACTION_LEG",
        indexes = {
                @Index(name = "idx_leg_account_amount", columnList = "account_number, amount"),
                @Index(name = "idx_leg_account_date", columnList = "account_number, created_at DESC"),
                @Index(name = "idx_leg_tx_id", columnList = "transaction_id")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TransactionLeg {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "leg_seq")
    @SequenceGenerator(name = "leg_seq", sequenceName = "SEQ_TRANSACTION_LEG", allocationSize = 1)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "transaction_id", nullable = false)
    private Transaction transaction;

    @Column(name = "account_number", nullable = false, length = 20)
    private String accountNumber;

    @Enumerated(EnumType.STRING)
    @Column(name = "entry_type", nullable = false, length = 10)
    private TransactionDirection entryType;

    @Column(name = "amount", nullable = false)
    private Integer amount;

    @Column(name = "post_balance", nullable = false)
    private Integer postBalance;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}