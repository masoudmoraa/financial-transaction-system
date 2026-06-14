package com.example.bank.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AuditLogPendingDto {
    private final String fieldName;
    private final String oldValue;
    private final String newValue;
}