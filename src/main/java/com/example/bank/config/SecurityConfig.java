package com.example.bank.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // ۱. غیرفعال کردن CSRF برای اینکه Postman بتواند درخواست POST بفرستد
                .csrf(csrf -> csrf.disable())

                // ۲. اجازه دسترسی به همه درخواست‌ها (فعلاً برای تست راحت)
                .authorizeHttpRequests(auth -> auth
                        .anyRequest().permitAll()
                )
                // ۳. فعال نگه داشتن Basic Auth در صورت نیاز
                .httpBasic(withDefaults());

        return http.build();
    }
}