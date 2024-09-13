package com.amanatpay.onramp.controller;

import com.amanatpay.onramp.dto.ApiResponse;
import com.amanatpay.onramp.dto.FinalRate;
import com.amanatpay.onramp.service.PriceCalculationService;
import com.amanatpay.onramp.service.SpreadService;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.Map;

@RestController
@RequestMapping("/rate")
public class RateController {

    @Autowired
    private PriceCalculationService priceCalculationService;

    @Autowired
    private SpreadService spreadService;


    @Value("${default.systemFee}")
    private BigDecimal defaultSystemFee;

    @Value("${default.transactionFee}")
    private double defaultTransactionFee;

    @GetMapping
    public ApiResponse<Map<String, BigDecimal>> getRate(@RequestParam @NotNull Long businessId) {
        try {
            BigDecimal wap = priceCalculationService.calculateWAP();
            BigDecimal rateWithSpread = spreadService.applyDynamicSpread(wap);
            BigDecimal finalRate = priceCalculationService.applyCommission(rateWithSpread, businessId);
            return new ApiResponse<>(200, "OK", Map.of("rate", finalRate), null);
        } catch (Exception e) {
            return new ApiResponse<>(500, "Failed to retrieve rate: " + e.getMessage(), null, e.getMessage());
        }
    }

    @GetMapping("/final")
    public ApiResponse<FinalRate> getRateFinal(@RequestParam @NotNull Long businessId, @RequestParam @NotNull BigDecimal amount) {
        try {
            FinalRate finalRate = priceCalculationService.calculateFinalRate(businessId, amount, defaultSystemFee, defaultTransactionFee);
            return new ApiResponse<>(200, "OK", finalRate, null);
        } catch (Exception e) {
            return new ApiResponse<>(500, "Failed to retrieve rate: " + e.getMessage(), null, e.getMessage());
        }
    }
}