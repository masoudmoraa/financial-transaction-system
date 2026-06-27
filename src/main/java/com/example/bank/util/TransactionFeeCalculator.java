package com.example.bank.util;

import com.example.bank.config.BankConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

// Component to calculate dynamic banking transaction fees based on configured limits and percentages.
@RequiredArgsConstructor
@Component
public class TransactionFeeCalculator {

    private final BankConfig bankConfig;

    public int calculateTransferFee(int amount) {
        BankConfig.TransferFee transferProps = bankConfig.getTransferFee();

        double rawFee = amount * (transferProps.getPercentage() / 100.0);
        double feeWithMax = Math.min(rawFee, transferProps.getMaxLimit());
        double finalFee = Math.max(feeWithMax, transferProps.getMinLimit());

        return (int) Math.round(finalFee);
    }
}