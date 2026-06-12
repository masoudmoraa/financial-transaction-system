package com.example.bank.service;

import com.example.bank.dto.*;
import com.example.bank.entity.Customer;
import com.example.bank.entity.CustomerAuditLog;
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
    private final CustomerAuditLogRepository auditLogRepository;
    private final AccountService accountService;

    public CustomerService(CustomerRepository customerRepository, CustomerAuditLogRepository auditLogRepository, AccountService accountService) {
        this.customerRepository = customerRepository;
        this.auditLogRepository = auditLogRepository;
        this.accountService = accountService;
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

        List<CustomerAuditLog> auditLogs = new ArrayList<>();
        String operator = "SYSTEM_USER";


        if (dto.getFirstName() != null && !dto.getFirstName().equals(customer.getFirstName())) {
            auditLogs.add(createLog(customer, "firstName", customer.getFirstName(), dto.getFirstName(), operator));
            customer.setFirstName(dto.getFirstName());
        }


        if (dto.getLastName() != null && !dto.getLastName().equals(customer.getLastName())) {
            auditLogs.add(createLog(customer, "lastName", customer.getLastName(), dto.getLastName(), operator));
            customer.setLastName(dto.getLastName());
        }


        if (dto.getNationalCode() != null && !dto.getNationalCode().equals(customer.getNationalCode())) {
            customerRepository.findByNationalCode(dto.getNationalCode()).ifPresent(c -> {
                throw new IllegalArgumentException("This national code is already taken by another customer.");
            });
            auditLogs.add(createLog(customer, "nationalCode", customer.getNationalCode(), dto.getNationalCode(), operator));
            customer.setNationalCode(dto.getNationalCode());
        }


        if (dto.getBirthday() != null && !dto.getBirthday().equals(customer.getBirthday())) {
            auditLogs.add(createLog(customer, "birthday", customer.getBirthday().toString(), dto.getBirthday().toString(), operator));
            customer.setBirthday(dto.getBirthday());
        }


        if (dto.getCustomerType() != null && !dto.getCustomerType().equals(customer.getCustomerType())) {
            auditLogs.add(createLog(customer, "customerType", customer.getCustomerType().name(), dto.getCustomerType().name(), operator));
            customer.setCustomerType(dto.getCustomerType());
        }


        if (dto.getPhoneNumber() != null && !dto.getPhoneNumber().equals(customer.getPhoneNumber())) {
            auditLogs.add(createLog(customer, "phoneNumber", customer.getPhoneNumber(), dto.getPhoneNumber(), operator));
            customer.setPhoneNumber(dto.getPhoneNumber());
        }


        if (dto.getAddress() != null && !dto.getAddress().equals(customer.getAddress())) {
            auditLogs.add(createLog(customer, "address", customer.getAddress(), dto.getAddress(), operator));
            customer.setAddress(dto.getAddress());
        }


        if (dto.getPostalCode() != null && !dto.getPostalCode().equals(customer.getPostalCode())) {
            auditLogs.add(createLog(customer, "postalCode", customer.getPostalCode(), dto.getPostalCode(), operator));
            customer.setPostalCode(dto.getPostalCode());
        }

        if (!auditLogs.isEmpty()) {
            auditLogRepository.saveAll(auditLogs);
        }
        customerRepository.save(customer);
    }

    private CustomerAuditLog createLog(Customer customer, String field, String oldVal, String newVal, String by) {
        return CustomerAuditLog.builder()
                .customer(customer)
                .changedField(field)
                .oldValue(oldVal)
                .newValue(newVal)
                .changedBy(by)
                .build();
    }
}