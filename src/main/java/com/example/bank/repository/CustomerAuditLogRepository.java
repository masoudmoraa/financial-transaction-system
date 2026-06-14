package com.example.bank.repository;

import com.example.bank.entity.CustomerAuditLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CustomerAuditLogRepository extends JpaRepository<CustomerAuditLog, Long> {

}