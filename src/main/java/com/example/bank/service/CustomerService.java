package com.example.bank.service;

import com.example.bank.dto.*;
import com.example.bank.entity.Customer;
import com.example.bank.repository.CustomerAuditLogRepository;
import com.example.bank.repository.CustomerRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.Optional;
import java.util.ArrayList;
import java.util.List;

@Service
public class CustomerService {

    private final CustomerRepository customerRepository;
    private final AccountService accountService;
    private final CustomerAuditLogService auditLogService;

    public CustomerService(CustomerRepository customerRepository, CustomerAuditLogRepository auditLogRepository, AccountService accountService, CustomerAuditLogService auditLogService) {
        this.customerRepository = customerRepository;
        this.accountService = accountService;
        this.auditLogService = auditLogService;
    }


    @Transactional
    public CreateAccountResponseDTO registerCustomer(CustomerRegisterRequestDTO dto) {

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
                .postalCode(dto.getPostal_code())
                .build();

        Customer savedCustomer = customerRepository.save(customer);

        return accountService.createAccount(savedCustomer);
    }

    @Transactional
    public void updateCustomer(CustomerUpdateRequestDTO dto) {

        Customer customer = customerRepository.findById(dto.getId())
                .orElseThrow(() -> new IllegalArgumentException("Customer not found with ID: " + dto.getId()));

        List<AuditLogPendingDto> pendingLogs = new ArrayList<>();

        if (dto.getFirstName() != null && !dto.getFirstName().equals(customer.getFirstName())) {
            pendingLogs.add(new AuditLogPendingDto("firstName", customer.getFirstName(), dto.getFirstName()));
            customer.setFirstName(dto.getFirstName());
        }

        if (dto.getLastName() != null && !dto.getLastName().equals(customer.getLastName())) {
            pendingLogs.add(new AuditLogPendingDto("lastName", customer.getLastName(), dto.getLastName()));
            customer.setLastName(dto.getLastName());
        }

        if (dto.getNationalCode() != null && !dto.getNationalCode().equals(customer.getNationalCode())) {
            customerRepository.findByNationalCode(dto.getNationalCode()).ifPresent(c -> {
                throw new IllegalArgumentException("This national code is already taken by another customer.");
            });
            pendingLogs.add(new AuditLogPendingDto("nationalCode", customer.getNationalCode(), dto.getNationalCode()));
            customer.setNationalCode(dto.getNationalCode());
        }

        if (dto.getBirthday() != null && !dto.getBirthday().equals(customer.getBirthday())) {
            pendingLogs.add(new AuditLogPendingDto("birthday",
                    customer.getBirthday() != null ? customer.getBirthday().toString() : null,
                    dto.getBirthday().toString()));
            customer.setBirthday(dto.getBirthday());
        }

        if (dto.getCustomerType() != null && !dto.getCustomerType().equals(customer.getCustomerType())) {
            pendingLogs.add(new AuditLogPendingDto("customerType",
                    customer.getCustomerType() != null ? customer.getCustomerType().name() : null,
                    dto.getCustomerType().name()));
            customer.setCustomerType(dto.getCustomerType());
        }

        if (dto.getPhoneNumber() != null && !dto.getPhoneNumber().equals(customer.getPhoneNumber())) {
            pendingLogs.add(new AuditLogPendingDto("phoneNumber", customer.getPhoneNumber(), dto.getPhoneNumber()));
            customer.setPhoneNumber(dto.getPhoneNumber());
        }

        if (dto.getAddress() != null && !dto.getAddress().equals(customer.getAddress())) {
            pendingLogs.add(new AuditLogPendingDto("address", customer.getAddress(), dto.getAddress()));
            customer.setAddress(dto.getAddress());
        }

        if (dto.getPostalCode() != null && !dto.getPostalCode().equals(customer.getPostalCode())) {
            pendingLogs.add(new AuditLogPendingDto("postalCode", customer.getPostalCode(), dto.getPostalCode()));
            customer.setPostalCode(dto.getPostalCode());
        }

        customerRepository.save(customer);

        if (!pendingLogs.isEmpty()) {
            auditLogService.saveAllLogs(customer, pendingLogs);
        }
    }
}