package com.example.bank.util;

import com.example.bank.config.BankConfig;
import org.springframework.stereotype.Component;

@Component
public class TransactionFeeCalculator {

    private final BankConfig bankConfig;

    public TransactionFeeCalculator(BankConfig bankConfig) {
        this.bankConfig = bankConfig;
    }

    public int calculateTransferFee(int amount) {
        BankConfig.TransferFee transferProps = bankConfig.getTransferFee();

        double rawFee = amount * (transferProps.getPercentage() / 100.0);
        double feeWithMax = Math.min(rawFee, transferProps.getMaxLimit());
        double finalFee = Math.max(feeWithMax, transferProps.getMinLimit());

        return (int) Math.round(finalFee);
    }
}