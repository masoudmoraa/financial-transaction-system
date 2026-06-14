package com.example.bank.config;

import com.example.bank.entity.Account;
import com.example.bank.entity.Customer;
import com.example.bank.enums.AccountStatus;
import com.example.bank.enums.CustomerType;
import com.example.bank.repository.AccountRepository;
import com.example.bank.repository.CustomerRepository;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Component
@Order(2)
public class BankAccountInitializer implements ApplicationRunner {

    private final AccountRepository accountRepository;
    private final CustomerRepository customerRepository;
    private final BankConfig bankConfig;

    private static final String BANK_NATIONAL_CODE = "0000000000";

    public BankAccountInitializer(AccountRepository accountRepository,
                                  CustomerRepository customerRepository,
                                  BankConfig bankConfig) {
        this.accountRepository = accountRepository;
        this.customerRepository = customerRepository;
        this.bankConfig = bankConfig;
    }

    @Override
    @Transactional
    public void run(ApplicationArguments args) throws Exception {
        String bankAccountNumber = bankConfig.getAccountNumber();

        System.out.println("[BankAccountInitializer] Checking system bank entities...");

        // Bank Customer
        Customer bankCustomer = customerRepository.findByNationalCode(BANK_NATIONAL_CODE)
                .orElseGet(() -> {
                    System.out.println("Central bank customer profile not found. Initializing profile...");

                    Customer newCustomer = Customer.builder()
                            .firstName("CENTRAL")
                            .lastName("BANK")
                            .nationalCode(BANK_NATIONAL_CODE)
                            .birthday(LocalDateTime.of(2026, 1, 1, 0, 0))
                            .customerType(CustomerType.LEGAL)
                            .phoneNumber("02160606060")
                            .address("Central Bank Headquarters")
                            .postalCode("1111111111")
                            .build();

                    return customerRepository.save(newCustomer);
                });

        // Bank Account
        boolean accountExists = accountRepository.existsById(bankAccountNumber);

        if (!accountExists) {
            System.out.println("Central bank revenue account not found. Initializing account setup...");

            Account bankAccount = Account.builder()
                    .accountNumber(bankAccountNumber)
                    .customer(bankCustomer)
                    .balance(0)
                    .status(AccountStatus.ACTIVE)
                    .build();

            accountRepository.save(bankAccount);
            System.out.println("Central bank profile and revenue account are fully synchronized in Oracle DB.");
        } else {
            System.out.println("Central bank setup verified and ready.");
        }
    }
}