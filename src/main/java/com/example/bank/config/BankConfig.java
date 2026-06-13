package com.example.bank.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "bank")
public class BankConfig {

    private String accountNumber;
    private TransferFee transferFee;

    @Data
    public static class TransferFee {
        private double percentage;
        private int minLimit;
        private int maxLimit;
    }

}