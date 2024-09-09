package com.amanatpay.onramp.service;

import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class MonitoringService {

    private final AnalysisService analysisService;
    private final NotificationService notificationService;

    public MonitoringService(AnalysisService analysisService, NotificationService notificationService) {
        this.analysisService = analysisService;
        this.notificationService = notificationService;
    }

    public void monitorMarket() {
        BigDecimal volatility = analysisService.calculateVolatility();
        if (volatility.compareTo(BigDecimal.valueOf(0.05)) > 0) { // Example threshold
            notificationService.alertAdmin("High volatility detected");
        }
    }
}