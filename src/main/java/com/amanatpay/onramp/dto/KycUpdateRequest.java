package com.amanatpay.onramp.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class KycUpdateRequest {

    @NotNull(message = "Mobile verification status is required")
    private Boolean mobileVerified;

    @NotNull(message = "Email verification status is required")
    private Boolean emailVerified;

    @NotNull(message = "Address verification status is required")
    private Boolean addressVerified;

    @NotNull(message = "KYC level is required")
    private Integer kycLevel;

    @NotNull(message = "Video verification status is required")
    private Boolean videoVerified;

    private String kycNotes;
}