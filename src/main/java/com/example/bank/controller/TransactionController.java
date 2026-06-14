package com.example.bank.controller;

import com.example.bank.dto.*;
import com.example.bank.enums.TransactionType;
import com.example.bank.service.TransactionService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/transactions")
public class TransactionController {

    private final TransactionService transactionService;
    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @PostMapping("/request")
    public ResponseEntity<ApiResponse<TransactionResponseDTO>> requestTransaction(@Valid @RequestBody TransactionRequestDTO requestDto) {

        // Business Logic Validation based on Transaction Type
        if (requestDto.getTransactionType() == TransactionType.TRANSFER) {
            if (requestDto.getSourceAccount() == null || requestDto.getDestinationAccount() == null) {
                return new ResponseEntity<>(ApiResponse.error("Both source and destination accounts are required for a transfer."), HttpStatus.BAD_REQUEST);
            }
            if (requestDto.getSourceAccount().equals(requestDto.getDestinationAccount())) {
                return new ResponseEntity<>(ApiResponse.error("Source and destination accounts cannot be the same."), HttpStatus.BAD_REQUEST);
            }
        } else if (requestDto.getTransactionType() == TransactionType.WITHDRAW && requestDto.getSourceAccount() == null) {
            return new ResponseEntity<>(ApiResponse.error("Source account is required for a withdraw."), HttpStatus.BAD_REQUEST);
        } else if (requestDto.getTransactionType() == TransactionType.DEPOSIT && requestDto.getDestinationAccount() == null) {
            return new ResponseEntity<>(ApiResponse.error("Destination account is required for a deposit."), HttpStatus.BAD_REQUEST);
        }



        TransactionResponseDTO responseDto = transactionService.submitRequest(requestDto);

        return new ResponseEntity<>(
                ApiResponse.success("Transaction successfully queued.", responseDto),
                HttpStatus.ACCEPTED
        );
    }


    @GetMapping("/track/{trackingCode}")
    public ResponseEntity<ApiResponse<TransactionStatusResponseDTO>> trackTransaction(@PathVariable String trackingCode) {

        TransactionStatusResponseDTO statusDto = transactionService.getTransactionStatus(trackingCode);

        return new ResponseEntity<>(
                ApiResponse.success("Transaction status retrieved successfully.", statusDto),
                HttpStatus.OK
        );
    }


    @PostMapping("/accountstatement")
    public ResponseEntity<ApiResponse<Page<AccountStatementResponseDTO>>>
    searchTransactions(
            @Valid @RequestBody AccountStatementRequestDTO dto,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size) {

        Page<AccountStatementResponseDTO> result =
                transactionService.searchTransactions(dto, page, size);

        return new ResponseEntity<>(
                ApiResponse.success("Transactions retrieved successfully.", result),
                HttpStatus.OK
        );
    }
}