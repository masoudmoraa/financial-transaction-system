package com.example.bank.service;

import com.example.bank.dto.CreateAccountResponseDTO;
import com.example.bank.entity.Customer;
import com.example.bank.repository.CustomerRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.Optional;
import com.example.bank.dto.CustomerRegisterRequestDTO;

@Service
public class CustomerService {

    private final CustomerRepository customerRepository;
    private final AccountService accountService;

    public CustomerService(CustomerRepository customerRepository, AccountService accountService) {
        this.customerRepository = customerRepository;
        this.accountService = accountService;
    }

    /**
     * دریافت DTO، اعتبارسنجی یکتایی کد ملی، تبدیل به Entity و افتتاح حساب
     */
    @Transactional
    public CreateAccountResponseDTO registerCustomer(CustomerRegisterRequestDTO dto) {
        // ۱. بررسی تکراری نبودن کد ملی
        Optional<Customer> existingCustomer = customerRepository.findByNationalCode(dto.getNationalCode());
        if (existingCustomer.isPresent()) {
            throw new IllegalArgumentException("Customer with this national code already exists.");
        }

        Customer customer = Customer.builder()
                .firstName(dto.getFirstName())
                .lastName(dto.getLastName())
                .nationalCode(dto.getNationalCode())
                .birthday(dto.getBirthday())
                .customerType(dto.getCustomerType())
                .phoneNumber(dto.getPhoneNumber())
                .address(dto.getAddress())
                .postal_code(dto.getPostal_code())
                .build();


        Customer savedCustomer = customerRepository.save(customer);

        return accountService.createAccount(savedCustomer);
    }
}