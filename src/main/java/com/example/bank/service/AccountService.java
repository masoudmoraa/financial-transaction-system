package com.example.bank.service;

import com.example.bank.dto.AccountDetailsResponseDTO;
import com.example.bank.dto.CreateAccountResponseDTO;
import com.example.bank.entity.Account;
import com.example.bank.entity.Customer;
import com.example.bank.repository.AccountRepository;
import com.example.bank.util.AccountNumberGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.example.bank.enums.AccountStatus;

// Service class to handle bank account creations and data retrievals.
@RequiredArgsConstructor
@Service
public class AccountService {

    private final AccountRepository accountRepository;
    private final AccountNumberGenerator accountNumberGenerator;

    // Creates and saves a new active bank account for a given customer.
    @Transactional
    public CreateAccountResponseDTO createAccount(Customer customer) {

        Account account = Account.builder()
                .accountNumber(accountNumberGenerator.generate())
                .balance(0)
                .status(AccountStatus.ACTIVE)
                .customer(customer)
                .build();
        accountRepository.save(account);

        return CreateAccountResponseDTO.builder()
                .accountNumber(account.getAccountNumber())
                .balance(account.getBalance())
                .build();
    }

    // Finds the bank account number associated with a specific national code.
    public String getAccountNumberByNationalCode(String nationalCode) {
        Account account = accountRepository.findByCustomerNationalCode(nationalCode)
                .orElseThrow(() -> new IllegalArgumentException("No account found for the given national code."));
        return account.getAccountNumber();
    }

    // Retrieves complete account information along with owner profile details.
    public AccountDetailsResponseDTO getAccountDetails(String accountNumber) {
        Account account = accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new IllegalArgumentException("Account not found with number: " + accountNumber));

        Customer customer = account.getCustomer();

        return AccountDetailsResponseDTO.builder()
                .accountNumber(account.getAccountNumber())
                .balance(account.getBalance())
                .status(account.getStatus())
                .createdAt(account.getCreatedAt())

                .firstName(customer.getFirstName())
                .lastName(customer.getLastName())
                .nationalCode(customer.getNationalCode())
                .birthday(customer.getBirthday())
                .customerType(customer.getCustomerType())
                .phoneNumber(customer.getPhoneNumber())
                .updatedAt(customer.getUpdatedAt())
                .address(customer.getAddress())
                .postalCode(customer.getPostalCode())
                .build();
    }

    // Returns the current available balance of a specific bank account.
    public Integer getAccountBalance(String accountNumber) {
        Account account = accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new IllegalArgumentException("Account not found with number: " + accountNumber));
        return account.getBalance();
    }
}