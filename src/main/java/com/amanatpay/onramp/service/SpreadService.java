package com.amanatpay.onramp.service;

import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class SpreadService {

    private final AnalysisService analysisService;

    public SpreadService(AnalysisService analysisService) {
        this.analysisService = analysisService;
    }

    public BigDecimal calculateDynamicSpread(BigDecimal wap) {
        BigDecimal volatility = analysisService.calculateVolatility();
        BigDecimal spread = BigDecimal.valueOf(2000); // Base spread
        if (volatility.compareTo(BigDecimal.valueOf(0.05)) > 0) { // Example threshold
            spread = spread.multiply(BigDecimal.valueOf(1.5)); // Increase spread
        }
        return wap.add(spread);
    }
}