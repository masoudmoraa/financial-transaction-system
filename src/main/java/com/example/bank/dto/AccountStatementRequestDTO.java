package com.example.bank.dto;

import com.example.bank.enums.TransactionType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class AccountStatementRequestDTO {

    @NotBlank(message = "Account number is required")
    @Pattern(regexp = "^\\d{14}$", message = "Account number must be numeric and 14 digits")
    private String accountNumber;

    @Pattern(regexp = "^\\d{14}$", message = "Account number must be numeric and 14 digits")
    private String sourceAccount;

    @Pattern(regexp = "^\\d{14}$", message = "Account number must be numeric and 14 digits")
    private String destinationAccount;

    private TransactionType transactionType;

    @PositiveOrZero(message = "Minimum amount cannot be negative")
    private Integer minAmount;

    @PositiveOrZero(message = "Maximum amount cannot be negative")
    private Integer maxAmount;

    private LocalDateTime fromDate;

    private LocalDateTime toDate;

}

