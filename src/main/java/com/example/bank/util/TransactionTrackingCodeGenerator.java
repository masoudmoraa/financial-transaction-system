package com.example.bank.util;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class TransactionTrackingCodeGenerator {

    public static String generate() {
        return "TC-" + UUID.randomUUID().toString().substring(0, 12).toUpperCase();
    }
}