package com.amanatpay.onramp.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class BusinessRegistrationRequest {

    @NotBlank(message = "Business name is required")
    @Size(max = 255, message = "Business name must be less than 255 characters")
    private String businessName;

    @Size(max = 255, message = "Website URL must be less than 255 characters")
    @Pattern(regexp = "^(http|https)://.*$", message = "Website URL must be a valid URL")
    private String websiteUrl;

    @NotBlank(message = "Owner name is required")
    private String ownerName;

    @NotBlank(message = "Owner email is required")
    @Email(message = "Owner email must be valid")
    private String ownerEmail;

    @NotBlank(message = "Owner password is required")
    private String ownerPassword;
}