package com.example.bank.repository;

import com.example.bank.entity.TransactionLeg;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface TransactionLegRepository extends JpaRepository<TransactionLeg, Long> {
    List<TransactionLeg> findByAccountNumberOrderByCreatedAtDesc(String accountNumber);
}