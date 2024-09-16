package com.amanatpay.onramp.dto;

import lombok.Data;

@Data
public class KycStatusResponse {
    private int kycLevel;
    private String statusMessage;

    public KycStatusResponse(int kycLevel, String statusMessage) {
        this.kycLevel = kycLevel;
        this.statusMessage = statusMessage;
    }


}