package com.example.bank.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI bankApi() {
        return new OpenAPI()
                .info(
                        new Info()
                                .title("Bank Management API")
                                .description("REST APIs for Bank Management System")
                                .version("1")
                                .contact(new Contact().name("Masoud Moradian").email("msd.mrdn01@gmail.com"))
                );
    }
}