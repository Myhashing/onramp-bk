package com.amanatpay.onramp.service;

import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class NotificationService {
//TODO: Implement the NotificationService class

    public void notifyUser(String userId, BigDecimal newRate) {
        // Implement logic to notify user via SMS or email
    }

    public void alertAdmin(String message) {
        // Implement logic to alert administrators
    }
}