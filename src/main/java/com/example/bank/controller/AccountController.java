package com.example.bank.controller;

import com.example.bank.dto.AccountDetailsResponseDTO;
import com.example.bank.dto.ApiResponse;
import com.example.bank.service.AccountService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/accounts")
@Slf4j
public class AccountController {

    private final AccountService accountService;

    // Takes a national code as a query parameter and returns the associated 14-digit bank account number.
    @GetMapping("/get-account-num")
    public ResponseEntity<ApiResponse<String>> getAccountNumber(@RequestParam String nationalCode) {
        log.info("Received request to fetch account number for National Code: {}", nationalCode);

        String accountNumber = accountService.getAccountNumberByNationalCode(nationalCode);
        log.info("Successfully retrieved account number for National Code: {}", nationalCode);

        return new ResponseEntity<>(ApiResponse.success("Account number retrieved successfully.", accountNumber), HttpStatus.OK);
    }

    // Takes a bank account number as a path variable and returns full profile and account details.
    @GetMapping("/{accountNumber}/details")
    public ResponseEntity<ApiResponse<AccountDetailsResponseDTO>> getAccountDetails(@PathVariable String accountNumber) {
        log.info("Received request to fetch account details for Account Number: {}", accountNumber);

        AccountDetailsResponseDTO details = accountService.getAccountDetails(accountNumber);
        log.info("Successfully retrieved account details for Account Number: {}", accountNumber);

        return new ResponseEntity<>(ApiResponse.success("Account details retrieved successfully.", details), HttpStatus.OK);
    }

    // Takes a bank account number as a path variable and returns its current available cash balance.
    @GetMapping("/{accountNumber}/balance")
    public ResponseEntity<ApiResponse<Integer>> getAccountBalance(@PathVariable String accountNumber) {
        log.info("Received request to fetch balance for Account Number: {}", accountNumber);

        Integer balance = accountService.getAccountBalance(accountNumber);
        log.info("Successfully retrieved balance for Account Number: {}", accountNumber);

        return new ResponseEntity<>(ApiResponse.success("Account balance retrieved successfully.", balance), HttpStatus.OK);
    }
}