package com.example.bank.dto;

import com.example.bank.enums.TransactionStatus;
import com.example.bank.enums.TransactionType;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
public class AccountStatementResponseDTO {

    private String trackingCode;

    private String sourceAccount;

    private String destinationAccount;

    private TransactionType transactionType;

    private Integer amount;

    private TransactionStatus status;

    private LocalDateTime createdAt;
}