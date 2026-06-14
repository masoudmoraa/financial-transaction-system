package com.example.bank.entity;

import com.example.bank.enums.TransactionType;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import com.example.bank.enums.TransactionStatus;

@Entity
@Table(
        name = "TRANSACTION",
        indexes = {
                @Index(name = "idx_tx_source_date", columnList = "source_account, created_at DESC"),
                @Index(name = "idx_tx_dest_date", columnList = "destination_account, created_at DESC")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "tx_req_seq")
    @SequenceGenerator(name = "tx_req_seq", sequenceName = "SEQ_TRANSACTION_REQUEST", allocationSize = 1)
    private Long id;

    @Column(name = "tracking_code", nullable = false, unique = true, length = 50)
    private String trackingCode;

    @Column(name = "source_account", length = 20)
    private String sourceAccount;

    @Column(name = "destination_account", length = 20)
    private String destinationAccount;

    @Enumerated(EnumType.STRING)
    @Column(name = "transaction_type", nullable = false, length = 20)
    private TransactionType transactionType;

    @Column(name = "amount", nullable = false)
    private Integer amount;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private TransactionStatus status;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}