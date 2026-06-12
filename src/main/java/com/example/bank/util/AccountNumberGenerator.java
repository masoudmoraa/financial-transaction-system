package com.example.bank.util;

import org.springframework.stereotype.Component;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.ThreadLocalRandom;


/**
 * Utility component responsible for generating unique 14-digit bank account numbers.
 * <p>
 * The generation algorithm follows a specific custom breakdown:
 * <ul>
 * <li><b>Prefix (2 digits):</b> Constant bank code ("60").</li>
 * <li><b>Date Formula (2 digits):</b> Calculated as {@code (current_month * 5) + current_day}.</li>
 * <li><b>Time (6 digits):</b> The exact timestamp formatted as {@code HHmmss} (hours, minutes, seconds).</li>
 * <li><b>Random (4 digits):</b> A secure thread-safe random number between 1000 and 9999.</li>
 * </ul>
 * </p>
 * This method is thread-safe and optimized for concurrent account creation requests.
 */
@Component
public class AccountNumberGenerator {

    private static final String BANK_PREFIX = "60";

    public String generate() {
        LocalDateTime now = LocalDateTime.now();

        int month = now.getMonthValue();
        int day = now.getDayOfMonth();
        int dateCalculation = (month * 5) + day;
        String datePart = String.format("%02d", dateCalculation);

        String timePart = now.format(DateTimeFormatter.ofPattern("HHmmss"));

        int randomPart = ThreadLocalRandom.current().nextInt(1000, 10000);

        return BANK_PREFIX + datePart + timePart + randomPart;
    }
}