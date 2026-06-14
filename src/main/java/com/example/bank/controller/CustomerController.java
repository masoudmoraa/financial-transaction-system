package com.example.bank.controller;

import com.example.bank.dto.*;
import com.example.bank.service.CustomerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.example.bank.dto.ApiResponse;

@RequiredArgsConstructor
@RestController
@RequestMapping("/customers")
@Slf4j
public class CustomerController {

    private final CustomerService customerService;


    @PostMapping("/register")
    public ResponseEntity<ApiResponse<CreateAccountResponseDTO>> registerCustomer(@Valid @RequestBody
                                                                                      CustomerRegisterRequestDTO dto) {

        log.info("Received request to REGISTER customer with National Code: {}", dto.getNationalCode());

        CreateAccountResponseDTO accountCreated = customerService.registerCustomer(dto);
        log.info("Successfully registered customer. Created Account Number: {}", accountCreated.getAccountNumber());

        ApiResponse<CreateAccountResponseDTO> response = ApiResponse.success(
                "Customer registered and account created successfully.",
                accountCreated
        );

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<ApiResponse<String>> updateCustomer(@PathVariable Long id,
            @Valid @RequestBody CustomerUpdateRequestDTO dto) {

        log.info("Received PUT request to UPDATE customer with ID: {}", id);

        customerService.updateCustomer(dto, id);
        log.info("Successfully updated customer data for ID: {}", id);

        return new ResponseEntity<>(ApiResponse.success("Updated successfully.", null), HttpStatus.OK);
    }
}