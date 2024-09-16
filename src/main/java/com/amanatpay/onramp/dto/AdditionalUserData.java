package com.amanatpay.onramp.dto;

import java.util.Map;

public class AdditionalUserData {
    private String firstName;
    private String lastName;
    private String email;

    // Getters and setters

    public Map<String, Object> toMap() {
        return Map.of(
            "firstName", firstName,
            "lastName", lastName,
            "email", email
        );
    }
}