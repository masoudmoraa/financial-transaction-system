package com.example.bank.dto;

import com.example.bank.enums.TransactionStatus;
import lombok.Builder;
import lombok.Data;


@Data
@Builder
public class TransactionResponseDTO {
    private String trackingCode;
    private TransactionStatus status;
}
