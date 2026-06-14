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
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

// Configuration class to bootstrap and customize web security filters for the application.
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final AdminRepository adminRepository;

    // Configures the core HTTP security filter chain, restricting all API endpoints to authenticated users with the ADMIN role.
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                // Disables CSRF protection since the API is stateless and does not use session-based cookies.
                .csrf(AbstractHttpConfigurer::disable)
                // Enforces that every single incoming HTTP request must be authorized under the specific role of ADMIN.
                .authorizeHttpRequests(auth -> auth.anyRequest().hasRole("ADMIN"))
                // Enables standard HTTP Basic Authentication with browser and Postman defaults.
                .httpBasic(Customizer.withDefaults())
                // Configures the application to be entirely stateless, preventing the server from creating or maintaining HTTP sessions.
                .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .build();
    }

    // Provides a BCrypt hashing encoder bean to secure and verify system passwords safely.
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // Customizes user loading logic by fetching credentials from the admin repository and mapping them to a Spring Security User principal.
    @Bean
    public UserDetailsService userDetailsService() {
        return username -> adminRepository.findByUsername(username)
                .map(admin -> User.withUsername(admin.getUsername())
                        .password(admin.getPassword()).roles("ADMIN").build())
                .orElseThrow(() -> new UsernameNotFoundException("Admin not found: " + username));
    }
}