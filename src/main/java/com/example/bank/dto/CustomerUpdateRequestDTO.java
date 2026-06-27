package com.example.bank.dto;

import com.example.bank.enums.CustomerType;
import lombok.Data;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;

@Data
public class CustomerUpdateRequestDTO {

    @Size(min = 2, max = 50, message = "First name must be between 2 and 50 characters.")
    private String firstName;

    @Size(min = 2, max = 50, message = "Last name must be between 2 and 50 characters.")
    private String lastName;

    @Pattern(regexp = "^\\d{10}$", message = "National code must be exactly 10 digits.")
    private String nationalCode;

    private LocalDateTime birthday;

    private CustomerType customerType;

    @Pattern(regexp = "^09\\d{9}$", message = "Phone number must be 11 digits and start with 09.")
    private String phoneNumber;

    @Size(max = 100, message = "Address cannot exceed 100 characters.")
    private String address;

    @Pattern(regexp = "^\\d{10}$", message = "Postal code must be exactly 10 digits.")
    private String postalCode;
}