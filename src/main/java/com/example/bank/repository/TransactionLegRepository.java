package com.example.bank.repository;

import com.example.bank.entity.TransactionLeg;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TransactionLegRepository extends JpaRepository<TransactionLeg, Long>{

}