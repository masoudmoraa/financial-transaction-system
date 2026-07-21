package com.example.bank.controller;

import com.example.bank.dto.*;
import com.example.bank.service.CustomerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(
        name = "Customer Management",
        description = "APIs for customer registration and profile management"
)
public class CustomerController {

    private final CustomerService customerService;

    // Takes customer profile data in the request body, registers the customer, and returns the newly created account number and initial balance.
    @Operation(
            summary = "Register a new customer",
            description = "Creates a new customer profile together with a new bank account."
    )
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

    // Takes a customer ID as a path variable and modified fields in the request body, updates the profile, and returns a basic success message.
    @Operation(
            summary = "Update customer information",
            description = "Updates editable customer information. Account number and creation date cannot be modified."
    )
    @PutMapping("/update/{id}")
    public ResponseEntity<ApiResponse<String>> updateCustomer(
            @Parameter(description = "Customer unique identifier, 0 has reserved for the Bank account", example = "1")
            @PathVariable Long id, @Valid @RequestBody CustomerUpdateRequestDTO dto) {

        log.info("Received PUT request to UPDATE customer with ID: {}", id);

        customerService.updateCustomer(dto, id);
        log.info("Successfully updated customer data for ID: {}", id);

        return new ResponseEntity<>(ApiResponse.success("Updated successfully.", null), HttpStatus.OK);
    }
}