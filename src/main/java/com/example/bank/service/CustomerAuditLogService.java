package com.example.bank.service;

import com.example.bank.dto.AuditLogPendingDto;
import com.example.bank.entity.Admin;
import com.example.bank.entity.Customer;
import com.example.bank.entity.CustomerAuditLog;
import com.example.bank.repository.AccountRepository;
import com.example.bank.repository.AdminRepository;
import com.example.bank.repository.CustomerAuditLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CustomerAuditLogService {

    private final CustomerAuditLogRepository auditLogRepository;
    private final AdminRepository adminRepository;
    private final AccountRepository accountRepository;

    @Transactional
    public void saveAllLogs(Customer customer, List<AuditLogPendingDto> pendingLogs) {

        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String username = (principal instanceof UserDetails userDetails) ?
                userDetails.getUsername() : principal.toString();

        Admin currentAdmin = adminRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Logged-in admin not found: " + username));

        List<CustomerAuditLog> auditLogs = pendingLogs.stream()
                .map(logDto -> CustomerAuditLog.builder()
                        .customer(customer)
                        .changedField(logDto.getFieldName())
                        .oldValue(logDto.getOldValue())
                        .newValue(logDto.getNewValue())
                        .changedBy(currentAdmin)
                        .build())
                .toList();

        auditLogRepository.saveAll(auditLogs);
    }
}