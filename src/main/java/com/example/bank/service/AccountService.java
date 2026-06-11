package com.example.bank.service;

import com.example.bank.dto.CreateAccountResponseDTO;
import com.example.bank.entity.Account;
import com.example.bank.entity.Customer;
import com.example.bank.repository.AccountRepository;
import com.example.bank.util.AccountNumberGenerator;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.example.bank.enums.AccountStatus;

@Service
public class AccountService {

    private final AccountRepository accountRepository;
    private final AccountNumberGenerator accountNumberGenerator;

    public AccountService(AccountRepository accountRepository, AccountNumberGenerator accountNumberGenerator) {
        this.accountRepository = accountRepository;
        this.accountNumberGenerator = accountNumberGenerator;
    }

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
}