package com.example.bank.config;

import com.example.bank.repository.AdminRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final AdminRepository adminRepository;

    // ۱. فیلتر چیدمان امنیت امنیتی (Security Filter Chain)
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // غیرفعال کردن CSRF چون وب‌سرویس ما Stateless (بدون وضعیت/سشن) است
                .csrf(AbstractHttpConfigurer::disable)

                // مدیریت دسترسی به URLها
                .authorizeHttpRequests(auth -> auth
                        // اجازه دسترسی به ارورها برای هندلر اختصاصی 404 که با هم نوشتیم
                        .requestMatchers("/error").permitAll()
                        // بقیه درخواست‌ها (مثل متد گردش حساب شما) حتما باید نقش ادمین داشته باشند
                        .anyRequest().hasRole("ADMIN")
                )

                // 🔥 فعال‌سازی پروتکل استاندارد HTTP Basic Auth
                .httpBasic(Customizer.withDefaults())

                // مدیریت سشن به صورت کاملا Stateless (عدم ذخیره کوکی در سرور)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        return http.build();
    }

    // ۲. انکودر برای بررسی هش پسورد ادمین‌ها
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // ۳. اتصال مستقیم به جدول BANK_ADMIN برای چک کردن یوزر و پسورد دیتا لودر
    @Bean
    public UserDetailsService userDetailsService() {
        return username -> adminRepository.findByUsername(username)
                .map(admin -> org.springframework.security.core.userdetails.User
                        .withUsername(admin.getUsername())
                        .password(admin.getPassword()) // پسورد هش شده در دیتابیس
                        .roles("ADMIN")
                        .build())
                .orElseThrow(() -> new UsernameNotFoundException("Admin not found with username: " + username));
    }
}