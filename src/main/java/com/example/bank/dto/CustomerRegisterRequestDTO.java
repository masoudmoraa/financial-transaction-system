package com.example.bank.dto;

import com.example.bank.enums.CustomerType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.time.LocalDateTime;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class CustomerRegisterRequestDTO {

    @NotBlank(message = "First name cannot be blank")
    @Size(max = 50, message = "First name cannot exceed 50 characters")
    private String firstName;

    @NotBlank(message = "Last name cannot be blank")
    @Size(max = 50, message = "Last name cannot exceed 50 characters")
    private String lastName;

    @NotBlank(message = "National code cannot be blank")
    @Pattern(regexp = "^\\d{10}$", message = "National code must be exactly 10 digits.")
    private String nationalCode;

    @NotNull(message = "Birthday is required")
    private LocalDateTime birthday;

    @NotNull(message = "Customer type is required (REAL/LEGAL)")
    private CustomerType customerType;

    @NotBlank(message = "Phone number cannot be blank")
    @Pattern(regexp = "^09\\d{9}$", message = "Phone number must be 11 digits and start with 09.")
    private String phoneNumber;

    @NotBlank(message = "Address cannot be blank")
    @Size(max = 100, message = "Address cannot exceed 100 characters")
    private String address;

    @NotBlank(message = "Postal code cannot be blank")
    @Pattern(regexp = "^\\d{10}$", message = "Postal code must be exactly 10 digits.")
    private String postal_code;
}