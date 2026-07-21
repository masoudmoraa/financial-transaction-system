package com.example.bank.repository.specification;

import com.example.bank.dto.AccountStatementRequestDTO;
import com.example.bank.entity.Transaction;
import com.example.bank.enums.TransactionType;
import com.example.bank.enums.TransactionDirection;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

// Specification class to build dynamic JPA queries for filtering database transaction records.
public class TransactionSpecification {

    // Private constructor to prevent instantiation of this class.
    private TransactionSpecification() {
    }

    // Builds a dynamic JPA Specification based on the filtering criteria provided in the request DTO.
    public static Specification<Transaction> build(AccountStatementRequestDTO dto) {

        return (root, query, cb) -> {

            List<Predicate> predicates = new ArrayList<>();

            predicates.add(
                    cb.or(cb.equal(root.get("sourceAccount"), dto.getAccountNumber()),
                            cb.equal(root.get("destinationAccount"), dto.getAccountNumber()))
            );

            if (dto.getSourceAccount() != null) {
                predicates.add(cb.equal(root.get("sourceAccount"), dto.getSourceAccount()));
            }

            if (dto.getDestinationAccount() != null) {
                predicates.add(cb.equal(root.get("destinationAccount"), dto.getDestinationAccount()));
            }

            if (dto.getTransactionType() != null) {
                predicates.add(cb.equal(root.get("transactionType"), dto.getTransactionType()));
            }

            if (dto.getMinAmount() != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("amount"), dto.getMinAmount()));
            }

            if (dto.getMaxAmount() != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("amount"), dto.getMaxAmount()));
            }

            if (dto.getFromDate() != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("createdAt"), dto.getFromDate()));
            }

            if (dto.getToDate() != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("createdAt"), dto.getToDate()));
            }

            if (dto.getEntryType() != null) {

            if (dto.getEntryType() == TransactionDirection.CREDIT) {
                predicates.add(
                    cb.or(
                        cb.equal(root.get("transactionType"), TransactionType.DEPOSIT),
                        cb.and(
                                cb.equal(root.get("transactionType"), TransactionType.TRANSFER),
                                cb.equal(root.get("destinationAccount"), dto.getAccountNumber())
                        )
                    )
                );

            }
            if (dto.getEntryType() == TransactionDirection.DEBIT) {
                predicates.add(
                    cb.or(
                        cb.equal(root.get("transactionType"), TransactionType.WITHDRAW),
                        cb.and(
                                cb.equal(root.get("transactionType"), TransactionType.TRANSFER),
                                cb.equal(root.get("sourceAccount"), dto.getAccountNumber())
                        )
                        )
                    );

                }
            }

            return cb.and(predicates.toArray(new Predicate[0])
            );
        };
    }
}