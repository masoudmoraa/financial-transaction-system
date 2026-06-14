package com.example.bank.validator;

import org.springframework.stereotype.Component;
import java.time.LocalDateTime;

// Component to validate business rules for customer registration.
@Component
public class CustomerValidator {

    private static final LocalDateTime AGE_LIMIT_YEAR = LocalDateTime.of(2010, 1, 1, 0, 0);

    // Validates that the provided birthday is before the year 2010.
    public void validateBirthday(LocalDateTime birthday) {
        if (birthday == null) {
            return;
        }

        if (!birthday.isBefore(AGE_LIMIT_YEAR)) {
            throw new IllegalArgumentException("Customer must be born before the year 2010.");
        }
    }
}