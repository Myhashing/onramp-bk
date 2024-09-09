package com.amanatpay.onramp.service;

import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class RateRebookingService {

    private final PriceCalculationService priceCalculationService;
    private final NotificationService notificationService;

    public RateRebookingService(PriceCalculationService priceCalculationService, NotificationService notificationService) {
        this.priceCalculationService = priceCalculationService;
        this.notificationService = notificationService;
    }

    public void rebookRate(String userId) {
        BigDecimal newRate = priceCalculationService.calculateWAP();
        // Notify user with the new rate
        notificationService.notifyUser(userId, newRate);
    }
}