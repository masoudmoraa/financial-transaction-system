package com.example.bank.controller;

import com.example.bank.dto.*;
import com.example.bank.enums.TransactionType;
import com.example.bank.service.TransactionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/transactions")
@Slf4j
public class TransactionController {

    private final TransactionService transactionService;

    @PostMapping("/request")
    public ResponseEntity<ApiResponse<TransactionResponseDTO>> requestTransaction(@Valid @RequestBody TransactionRequestDTO requestDto) {
        log.info("Received transaction request. Type: {}, Amount: {}", requestDto.getTransactionType(), requestDto.getAmount());

        // Business Logic Validation based on Transaction Type
        if (requestDto.getTransactionType() == TransactionType.TRANSFER) {
            if (requestDto.getSourceAccount() == null || requestDto.getDestinationAccount() == null) {
                log.warn("Transfer rejected: Missing source or destination account.");
                return new ResponseEntity<>(ApiResponse.error("Both source and destination accounts are required for a transfer."), HttpStatus.BAD_REQUEST);
            }
            if (requestDto.getSourceAccount().equals(requestDto.getDestinationAccount())) {
                log.warn("Transfer rejected: Source and destination accounts are identical: {}", requestDto.getSourceAccount());
                return new ResponseEntity<>(ApiResponse.error("Source and destination accounts cannot be the same."), HttpStatus.BAD_REQUEST);
            }
        } else if (requestDto.getTransactionType() == TransactionType.WITHDRAW && requestDto.getSourceAccount() == null) {
            log.warn("Withdraw rejected: Missing source account.");
            return new ResponseEntity<>(ApiResponse.error("Source account is required for a withdraw."), HttpStatus.BAD_REQUEST);
        } else if (requestDto.getTransactionType() == TransactionType.DEPOSIT && requestDto.getDestinationAccount() == null) {
            log.warn("Deposit rejected: Missing destination account.");
            return new ResponseEntity<>(ApiResponse.error("Destination account is required for a deposit."), HttpStatus.BAD_REQUEST);
        }

        TransactionResponseDTO responseDto = transactionService.submitRequest(requestDto);
        log.info("Transaction successfully queued. Tracking Code: {}, Type: {}", responseDto.getTrackingCode(), requestDto.getTransactionType());

        return new ResponseEntity<>(
                ApiResponse.success("Transaction successfully queued.", responseDto),
                HttpStatus.ACCEPTED
        );
    }


    @GetMapping("/track/{trackingCode}")
    public ResponseEntity<ApiResponse<TransactionStatusResponseDTO>> trackTransaction(@PathVariable String trackingCode) {
        log.info("Received request to track transaction with Tracking Code: {}", trackingCode);

        TransactionStatusResponseDTO statusDto = transactionService.getTransactionStatus(trackingCode);
        log.info("Successfully retrieved status for Tracking Code: {}. Status: {}", trackingCode, statusDto.getStatus());

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

        log.info("Received request for Account Statement. Account Number: {}, Page: {}, Size: {}", dto.getAccountNumber(), page, size);

        Page<AccountStatementResponseDTO> result =
                transactionService.searchTransactions(dto, page, size);
        log.info("Successfully retrieved account statement for Account Number: {}. Found {} records on page {}",
                dto.getAccountNumber(), result.getNumberOfElements(), page);

        return new ResponseEntity<>(
                ApiResponse.success("Transactions retrieved successfully.", result),
                HttpStatus.OK
        );
    }
}