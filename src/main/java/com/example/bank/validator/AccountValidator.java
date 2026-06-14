package com.example.bank.validator;

import com.example.bank.entity.Account;
import com.example.bank.enums.AccountStatus;
import org.springframework.stereotype.Component;

// Component to validate bank account numbers and national codes.
@Component
public class AccountValidator {

    // Validates that the national code is exactly 10 digits.
    public void validateNationalCode(String nationalCode) {
        if (nationalCode == null || !nationalCode.matches("\\d{10}")) {
            throw new IllegalArgumentException("Invalid national code format. It must be exactly 10 digits.");
        }
    }

    // Validates that the bank account number is exactly 14 digits.
    public void validateAccountNumber(String accountNumber) {
        if (accountNumber == null || !accountNumber.matches("\\d{14}")) {
            throw new IllegalArgumentException("Invalid account number format. It must be exactly 14 digits.");
        }
    }

    public void validateAccountActive(Account account) {
        if (account == null || account.getStatus() != AccountStatus.ACTIVE) {
            throw new IllegalStateException("Account status restriction: Account is not ACTIVE.");
        }
    }
}