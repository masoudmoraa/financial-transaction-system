package com.example.bank.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import java.util.List;

// Configuration class to load default admin accounts from application properties.
@Data
@Configuration
@ConfigurationProperties(prefix = "app-security")
public class AdminConfig {

    private List<AdminDto> defaultAdmins;

    @Data
    public static class AdminDto {
        private String username;
        private String password;
        private String nationalCode;
        private String firstName;
        private String lastName;
    }
}