package com.amanatpay.onramp.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;


@Data
public class UserRegistrationRequest {
    @NotBlank(message = "Mobile phone is required")
    private String mobilePhone;

    @NotBlank(message = "National ID is required")
    private String nationalId;

    @NotBlank(message = "Password is required")
    private String password;

    private String email; // Optional field
}