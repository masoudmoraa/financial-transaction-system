package com.example.bank.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CreateAccountResponseDTO {
    private String accountNumber;
    private Integer balance;
}