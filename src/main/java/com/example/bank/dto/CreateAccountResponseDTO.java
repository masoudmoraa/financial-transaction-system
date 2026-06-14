package com.example.bank.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
public class CreateAccountResponseDTO {
    private String accountNumber;
    private Integer balance;
}