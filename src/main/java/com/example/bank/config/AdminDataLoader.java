package com.example.bank.config;

import com.example.bank.entity.Admin;
import com.example.bank.repository.AdminRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@Order(1)
@RequiredArgsConstructor
@Slf4j
public class AdminDataLoader implements ApplicationRunner {

    private final AdminRepository adminRepository;
    private final AdminConfig adminConfig;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        if (adminConfig.getDefaultAdmins() == null) {
            log.warn("No default admins found in configuration properties.");
            return;
        }

        log.info("Starting Admin Database Initialization (Order: 1)...");

        for (AdminConfig.AdminDto adminDto : adminConfig.getDefaultAdmins()) {
            // بررسی عدم وجود برای جلوگیری از رکوردهای تکراری در ری‌استارت‌ها
            if (adminRepository.findByUsername(adminDto.getUsername()).isEmpty()) {

                Admin admin = Admin.builder()
                        .username(adminDto.getUsername())
                        // هش کردن پسورد با BCryptPasswordEncoder پیش از ذخیره در دیتابیس
                        .password(passwordEncoder.encode(adminDto.getPassword()))
                        .nationalCode(adminDto.getNationalCode())
                        .firstName(adminDto.getFirstName())
                        .lastName(adminDto.getLastName())
                        .build();

                adminRepository.save(admin);
                log.info("Successfully seeded admin user: {}", adminDto.getUsername());
            } else {
                log.debug("Admin '{}' already exists. Skipping initialization.", adminDto.getUsername());
            }
        }

        log.info("Admin Database Initialization completed.");
    }
}