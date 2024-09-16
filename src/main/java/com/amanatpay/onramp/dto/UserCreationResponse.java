package com.amanatpay.onramp.dto;

import java.util.UUID;

public class UserCreationResponse {
    private UUID userId;
    private String message;

    public UserCreationResponse(UUID userId, String message) {
        this.userId = userId;
        this.message = message;
    }

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}