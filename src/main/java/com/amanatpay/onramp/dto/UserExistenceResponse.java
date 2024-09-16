package com.amanatpay.onramp.dto;

import lombok.Data;

import java.util.UUID;

@Data
public class UserExistenceResponse {
    private boolean exists;
    private UUID userId;

    public UserExistenceResponse(boolean exists, UUID userId) {
        this.exists = exists;
        this.userId = userId;
    }
}
