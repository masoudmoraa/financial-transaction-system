package com.example.bank.dto;

import com.example.bank.enums.AccountStatus;
import com.example.bank.enums.CustomerType;
import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Builder
public class AccountDetailsResponseDTO {
    // (Account)
    private String accountNumber;
    private Integer balance;
    private AccountStatus status;
    private LocalDateTime createdAt;

    // (Customer)
    private String firstName;
    private String lastName;
    private String nationalCode;
    private LocalDateTime birthday;
    private CustomerType customerType;
    private String phoneNumber;
    private LocalDateTime updatedAt;
    private String address;
    private String postalCode;
}