package com.example.bank.validator;

import com.example.bank.dto.AccountStatementRequestDTO;
import org.springframework.stereotype.Component;

@Component
public class AccountStatementValidator {

    // Validates that amount ranges and date boundaries in the request criteria are logically consistent.
    public void validate(
            AccountStatementRequestDTO dto) {

        if (dto.getMinAmount() != null
                && dto.getMaxAmount() != null
                && dto.getMinAmount() > dto.getMaxAmount()) {

            throw new IllegalArgumentException(
                    "minAmount cannot be greater than maxAmount."
            );
        }

        if (dto.getFromDate() != null
                && dto.getToDate() != null
                && dto.getFromDate().isAfter(dto.getToDate())) {

            throw new IllegalArgumentException(
                    "fromDate cannot be after toDate."
            );
        }
    }
}