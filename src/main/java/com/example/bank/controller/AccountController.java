package com.example.bank.controller;

import com.example.bank.dto.AccountDetailsResponseDTO;
import com.example.bank.dto.ApiResponse;
import com.example.bank.service.AccountService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/accounts")
public class AccountController {

    private final AccountService accountService;

    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    @GetMapping("/get-account-num")
    public ResponseEntity<ApiResponse<String>> getAccountNumber(@RequestParam String nationalCode) {
        String accountNumber = accountService.getAccountNumberByNationalCode(nationalCode);
        return new ResponseEntity<>(ApiResponse.success("Account number retrieved successfully.", accountNumber), HttpStatus.OK);
    }

    @GetMapping("/{accountNumber}/details")
    public ResponseEntity<ApiResponse<AccountDetailsResponseDTO>> getAccountDetails(@PathVariable String accountNumber) {
        AccountDetailsResponseDTO details = accountService.getAccountDetails(accountNumber);
        return new ResponseEntity<>(ApiResponse.success("Account details retrieved successfully.", details), HttpStatus.OK);
    }

    @GetMapping("/{accountNumber}/balance")
    public ResponseEntity<ApiResponse<Integer>> getAccountBalance(@PathVariable String accountNumber) {
        Integer balance = accountService.getAccountBalance(accountNumber);
        return new ResponseEntity<>(ApiResponse.success("Account balance retrieved successfully.", balance), HttpStatus.OK);
    }
}