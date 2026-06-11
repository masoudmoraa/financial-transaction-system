package com.example.bank.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import com.example.bank.enums.TransactionType;
import com.example.bank.enums.TransactionDirection;

@Entity
@Table(name = "TRANSACTION_LEG")
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
    private TransactionRequest transaction;

    @Column(name = "account_number", nullable = false, length = 20)
    private String accountNumber;

    @Enumerated(EnumType.STRING)
    @Column(name = "entry_type", nullable = false, length = 10)
    private TransactionDirection entryType;

    @Enumerated(EnumType.STRING)
    @Column(name = "transaction_type", nullable = false, length = 20)
    private TransactionType transactionType;

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