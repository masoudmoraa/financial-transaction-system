package com.example.bank.dto;

import com.example.bank.enums.TransactionStatus;
import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Builder
public class TransactionStatusResponseDTO {
    private String trackingCode;
    private TransactionStatus status;
    private LocalDateTime createdAt;
}