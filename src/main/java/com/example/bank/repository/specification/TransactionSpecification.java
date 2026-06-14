package com.example.bank.repository.specification;

import com.example.bank.dto.AccountStatementRequestDTO;
import com.example.bank.entity.Transaction;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public class TransactionSpecification {

    private TransactionSpecification() {
    }

    public static Specification<Transaction> build(AccountStatementRequestDTO dto) {

        return (root, query, cb) -> {

            List<Predicate> predicates = new ArrayList<>();

            predicates.add(
                    cb.or(
                            cb.equal(
                                    root.get("sourceAccount"),
                                    dto.getAccountNumber()
                            ),
                            cb.equal(
                                    root.get("destinationAccount"),
                                    dto.getAccountNumber()
                            )
                    )
            );

            if (dto.getSourceAccount() != null) {
                predicates.add(
                        cb.equal(
                                root.get("sourceAccount"),
                                dto.getSourceAccount()
                        )
                );
            }

            if (dto.getDestinationAccount() != null) {
                predicates.add(
                        cb.equal(
                                root.get("destinationAccount"),
                                dto.getDestinationAccount()
                        )
                );
            }

            if (dto.getTransactionType() != null) {
                predicates.add(
                        cb.equal(
                                root.get("transactionType"),
                                dto.getTransactionType()
                        )
                );
            }

            if (dto.getMinAmount() != null) {
                predicates.add(
                        cb.greaterThanOrEqualTo(
                                root.get("amount"),
                                dto.getMinAmount()
                        )
                );
            }

            if (dto.getMaxAmount() != null) {
                predicates.add(
                        cb.lessThanOrEqualTo(
                                root.get("amount"),
                                dto.getMaxAmount()
                        )
                );
            }

            if (dto.getFromDate() != null) {
                predicates.add(
                        cb.greaterThanOrEqualTo(
                                root.get("createdAt"),
                                dto.getFromDate()
                        )
                );
            }

            if (dto.getToDate() != null) {
                predicates.add(
                        cb.lessThanOrEqualTo(
                                root.get("createdAt"),
                                dto.getToDate()
                        )
                );
            }

            return cb.and(predicates.toArray(new Predicate[0])
            );
        };
    }
}