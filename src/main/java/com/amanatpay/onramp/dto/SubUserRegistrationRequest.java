package com.amanatpay.onramp.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class SubUserRegistrationRequest {

    @NotBlank(message = "Business ID is required")
    private String businessId;

    @NotBlank(message = "Access level is required")
    @Pattern(regexp = "^(view-only|full-access)$", message = "Access level must be either 'view-only' or 'full-access'")
    private String accessLevel;

    @NotBlank(message = "User name is required")
    private String userName;


    @Email(message = "User email must be valid")
    private String userEmail;

    @NotBlank(message = "User password is required")
    private String userPassword;

    @NotBlank(message = "Mobile phone is required")
    private String mobilePhone;

}