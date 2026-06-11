package com.example.bank.controller;

import com.example.bank.dto.*;
import com.example.bank.service.CustomerService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.example.bank.dto.ApiResponse;

@RestController
@RequestMapping("/customers")
public class CustomerController {

    private final CustomerService customerService;

    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<CreateAccountResponseDTO>> registerCustomer(@Valid @RequestBody CustomerRegisterRequestDTO dto) {

        CreateAccountResponseDTO accountCreated = customerService.registerCustomer(dto);

        ApiResponse<CreateAccountResponseDTO> response = ApiResponse.success(
                "Customer registered and account created successfully.",
                accountCreated
        );

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PostMapping("/update")
    public ResponseEntity<ApiResponse<String>> updateCustomer(@Valid @RequestBody CustomerUpdateRequestDTO dto) {
        customerService.updateCustomer(dto);
        return new ResponseEntity<>(ApiResponse.success("Updated successfully.", null), HttpStatus.OK);
    }
}