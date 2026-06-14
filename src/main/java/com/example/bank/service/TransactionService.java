package com.example.bank.service;

import com.example.bank.config.BankConfig;
import com.example.bank.config.RabbitMQConfig;
import com.example.bank.dto.TransactionRequestDTO;
import com.example.bank.dto.TransactionResponseDTO;
import com.example.bank.dto.TransactionStatusResponseDTO;
import com.example.bank.dto.AccountStatementRequestDTO;
import com.example.bank.dto.AccountStatementResponseDTO;
import org.springframework.data.domain.*;
import com.example.bank.entity.Account;
import com.example.bank.entity.TransactionLeg;
import com.example.bank.entity.Transaction;
import com.example.bank.enums.AccountStatus;
import com.example.bank.enums.TransactionDirection;
import com.example.bank.enums.TransactionStatus;
import com.example.bank.enums.TransactionType;
import com.example.bank.repository.AccountRepository;
import com.example.bank.repository.TransactionLegRepository;
import com.example.bank.repository.TransactionRepository;
import com.example.bank.repository.specification.TransactionSpecification;
import com.example.bank.util.TransactionFeeCalculator;
import com.example.bank.util.TransactionTrackingCodeGenerator;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.jpa.domain.Specification;
import com.example.bank.validator.AccountStatementValidator;

@Service
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final AccountRepository accountRepository;
    private final TransactionLegRepository legRepository;
    private final RabbitTemplate rabbitTemplate;
    private final TransactionFeeCalculator feeCalculator;
    private final BankConfig bankConfig;
    private final AccountStatementValidator validator;

    private static final String BANK_FEE_ACCOUNT = "60606060606060";

    public TransactionService(TransactionRepository transactionRepository,
                              AccountRepository accountRepository,
                              TransactionLegRepository legRepository,
                              RabbitTemplate rabbitTemplate,
                              TransactionFeeCalculator feeCalculator,
                              BankConfig bankConfig,
                              AccountStatementValidator validator) {
        this.transactionRepository = transactionRepository;
        this.accountRepository = accountRepository;
        this.legRepository = legRepository;
        this.rabbitTemplate = rabbitTemplate;
        this.feeCalculator = feeCalculator;
        this.bankConfig = bankConfig;
        this.validator = validator;
    }


    @Transactional
    public TransactionResponseDTO submitRequest(TransactionRequestDTO requestDto) {

        // Generate tracking code
        String trackingCode = TransactionTrackingCodeGenerator.generate();
        requestDto.setTrackingCode(trackingCode);

        Transaction transaction = new Transaction();
        transaction.setTrackingCode(trackingCode);
        transaction.setSourceAccount(requestDto.getSourceAccount());
        transaction.setDestinationAccount(requestDto.getDestinationAccount());
        transaction.setTransactionType(requestDto.getTransactionType());
        transaction.setAmount(requestDto.getAmount());
        transaction.setStatus(TransactionStatus.PENDING);

        transactionRepository.save(transaction);

        // Asynchronous processing
        rabbitTemplate.convertAndSend(
                RabbitMQConfig.BANK_EXCHANGE,
                RabbitMQConfig.TRANSACTION_ROUTING_KEY,
                requestDto
        );

        return TransactionResponseDTO.builder()
                .trackingCode(trackingCode)
                .status(TransactionStatus.PENDING)
                .build();
    }


    @Transactional
    public void processTransaction(TransactionRequestDTO dto) {
        Transaction txRequest = transactionRepository.findByTrackingCode(dto.getTrackingCode())
                .orElseThrow(() -> new IllegalArgumentException("Transaction request not found for code: " + dto.getTrackingCode()));

        txRequest.setStatus(TransactionStatus.PROCESSING);
        transactionRepository.saveAndFlush(txRequest);

        try {
            if (dto.getTransactionType() == TransactionType.DEPOSIT) {
                handleDeposit(dto, txRequest);
            } else if (dto.getTransactionType() == TransactionType.WITHDRAW) {
                handleWithdraw(dto, txRequest);
            } else if (dto.getTransactionType() == TransactionType.TRANSFER) {
                handleTransfer(dto, txRequest);
            }

            txRequest.setStatus(TransactionStatus.SUCCESS);
            transactionRepository.save(txRequest);

        } catch (Exception e) {
            System.out.println("hereeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeee");
            txRequest.setStatus(TransactionStatus.FAILED);
            transactionRepository.save(txRequest);

            throw new RuntimeException("Async banking execution aborted: " + e.getMessage(), e);
        }
    }


    @Transactional(readOnly = true)
    public TransactionStatusResponseDTO getTransactionStatus(String trackingCode) {
        Transaction tx = transactionRepository.findByTrackingCode(trackingCode)
                .orElseThrow(() -> new IllegalArgumentException("Invalid tracking code: " + trackingCode));

        return TransactionStatusResponseDTO.builder()
                .trackingCode(tx.getTrackingCode())
                .status(tx.getStatus())
                .createdAt(tx.getCreatedAt())
                .build();
    }



    private void handleDeposit(TransactionRequestDTO dto, Transaction tx) {
        Account destAccount = getActiveAccountWithLock(dto.getDestinationAccount());

        destAccount.setBalance(destAccount.getBalance() + dto.getAmount());
        accountRepository.save(destAccount);

        createLeg(tx, destAccount.getAccountNumber(), TransactionDirection.CREDIT,
                dto.getAmount(), destAccount.getBalance());
    }

    private void handleWithdraw(TransactionRequestDTO dto, Transaction tx) {
        Account srcAccount = getActiveAccountWithLock(dto.getSourceAccount());

        if (srcAccount.getBalance() < dto.getAmount()) {
            throw new IllegalArgumentException("Insufficient account balance.");
        }

        srcAccount.setBalance(srcAccount.getBalance() - dto.getAmount());
        accountRepository.save(srcAccount);

        createLeg(tx, srcAccount.getAccountNumber(), TransactionDirection.DEBIT,
                dto.getAmount(), srcAccount.getBalance());
    }

    private void handleTransfer(TransactionRequestDTO dto, Transaction tx) {
        // Safe ordering for lock allocation to guarantee zero deadlocks across parallel channels
        // Sort account numbers to guarantee a consistent locking order (Deadlock Prevention)
        String[] accountsForLocking = { dto.getSourceAccount(), dto.getDestinationAccount() };
        java.util.Arrays.sort(accountsForLocking);

        Account srcAccount  = getActiveAccountWithLock(dto.getSourceAccount().equals(
                accountsForLocking[0]) ? accountsForLocking[0] : accountsForLocking[1]);
        Account destAccount = getActiveAccountWithLock(dto.getDestinationAccount().equals(
                accountsForLocking[0]) ? accountsForLocking[0] : accountsForLocking[1]);

        int calculatedFee = feeCalculator.calculateTransferFee(dto.getAmount());
        int totalDeduction = dto.getAmount() + calculatedFee;

        if (srcAccount.getBalance() < totalDeduction) {
            throw new IllegalArgumentException("Insufficient balance to cover transfer amount and fixed service fee.");
        }

        srcAccount.setBalance(srcAccount.getBalance() - totalDeduction);
        destAccount.setBalance(destAccount.getBalance() + dto.getAmount());
        accountRepository.save(srcAccount);
        accountRepository.save(destAccount);


        createLeg(tx, srcAccount.getAccountNumber(), TransactionDirection.DEBIT,
                dto.getAmount(), srcAccount.getBalance() + calculatedFee);
        createLeg(tx, srcAccount.getAccountNumber(), TransactionDirection.DEBIT,
                calculatedFee, srcAccount.getBalance());

        // (Fee from Src to Bank)
        Transaction feeTx = new Transaction();
        feeTx.setTrackingCode("FEE#" + tx.getTrackingCode());
        feeTx.setSourceAccount(srcAccount.getAccountNumber());
        feeTx.setDestinationAccount(bankConfig.getAccountNumber());
        feeTx.setTransactionType(TransactionType.TRANSFER);
        feeTx.setAmount(calculatedFee);
        feeTx.setStatus(TransactionStatus.SUCCESS);
        transactionRepository.save(feeTx);


        createLeg(feeTx, srcAccount.getAccountNumber(), TransactionDirection.DEBIT,
                calculatedFee, srcAccount.getBalance());

        createLeg(feeTx, BANK_FEE_ACCOUNT, TransactionDirection.CREDIT,
                calculatedFee, destAccount.getBalance());
    }

    private Account getActiveAccountWithLock(String accountNumber) {
        Account account = accountRepository.findWithLockByAccountNumber(accountNumber)
                .orElseThrow(() -> new IllegalArgumentException("Account not found: " + accountNumber));

        if (account.getStatus() != AccountStatus.ACTIVE) {
            throw new IllegalStateException("Account " + accountNumber + " status restriction: Account is not ACTIVE.");
        }
        return account;
    }

    private void createLeg(Transaction tx, String accountNum, TransactionDirection direction,
                           Integer amount, Integer postBalance) {
        TransactionLeg leg = new TransactionLeg();
        leg.setTransaction(tx);
        leg.setAccountNumber(accountNum);
        leg.setEntryType(direction);
        leg.setAmount(amount);
        leg.setPostBalance(postBalance);
        legRepository.save(leg);
    }


    public Page<AccountStatementResponseDTO> searchTransactions(
            AccountStatementRequestDTO dto, int page, int size) {

        validator.validate(dto);

        Specification<Transaction> specification =
                TransactionSpecification.build(dto);

        Pageable pageable = PageRequest.of(
                page,
                size,
                Sort.by(Sort.Direction.DESC, "createdAt")
        );

        Page<Transaction> transactions = transactionRepository.findAll(specification, pageable);

        return transactions.map(tx ->
                AccountStatementResponseDTO.builder()
                        .trackingCode(tx.getTrackingCode())
                        .sourceAccount(tx.getSourceAccount())
                        .destinationAccount(tx.getDestinationAccount())
                        .transactionType(tx.getTransactionType())
                        .amount(tx.getAmount())
                        .status(tx.getStatus())
                        .createdAt(tx.getCreatedAt())
                        .build()
        );
    }

}