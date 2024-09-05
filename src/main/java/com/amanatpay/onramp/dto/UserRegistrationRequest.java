package com.amanatpay.onramp.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;


@Data
public class UserRegistrationRequest {
    @NotBlank(message = "Mobile phone is required")
    private String mobilePhone;

    @NotBlank(message = "National ID is required")
    private String nationalId;

    @NotBlank(message = "Password is required")
    private String password;

    @NotBlank(message = "Business ID is required")
    private String businessId;


    @NotBlank(message = "User type is required")
    @Pattern(regexp = "^(individual|corporate)$", message = "User type must be either 'individual' or 'corporate'")
    private String userType;

    private String email; // Optional field
}