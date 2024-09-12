package com.amanatpay.onramp.controller;

import com.amanatpay.onramp.dto.ApiResponse;
import com.amanatpay.onramp.entity.OrderBookData;
import com.amanatpay.onramp.service.PriceCalculationService;
import com.amanatpay.onramp.service.RateLockService;
import com.amanatpay.onramp.repository.OrderBookDataRepository;
import com.amanatpay.onramp.service.SpreadService;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Map;

@RestController
@RequestMapping("/rate")
public class RateController {

    @Autowired
    private PriceCalculationService priceCalculationService;

    @Autowired
    private SpreadService spreadService;

    /**
     * Get the current rate for a business.
     * The rate is calculated based on the weighted average price (WAP),
     * the dynamic spread, and the commission rate for the business.
     *
     *
     * @param businessId the ID of the business
     * @return the current rate
     */
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


    /**
     * Get the final rate for a business based on the amount to be exchanged.
     * The final rate is calculated based on the weighted average price (WAP),
     * the dynamic spread, the commission rate for the business, and additional fees.
     *
     *
     * @param businessId the ID of the business
     * @param amount the amount to be exchanged
     * @return the final rate
     */
    @GetMapping("/final")
    public ApiResponse<Map<String, BigDecimal>> getRateFinal(@RequestParam @NotNull Long businessId, @RequestParam @NotNull BigDecimal amount) {
        try {
            BigDecimal wap = priceCalculationService.calculateWAPByAmount(amount);
            BigDecimal rateWithSpread = spreadService.applyDynamicSpread(wap);
            BigDecimal finalRate = priceCalculationService.applyCommission(rateWithSpread, businessId);

            // Calculate system fee (example: 1% of the amount)
            BigDecimal systemFee = amount.multiply(BigDecimal.valueOf(0.01));

            // Calculate transaction fee (example: fixed fee of 5 USDT)
            BigDecimal transactionFee = BigDecimal.valueOf(5);

            // Calculate the total amount the user will pay
            BigDecimal totalAmount = amount.add(systemFee).add(transactionFee);

            // Calculate the amount of USDT the user will get
            BigDecimal usdtAmount = amount.divide(finalRate, RoundingMode.HALF_UP);
            return new ApiResponse<>(200, "OK", Map.of(
                "rate", finalRate,
                "systemFee", systemFee,
                "transactionFee", transactionFee,
                "totalAmount", totalAmount,
                "usdtAmount", usdtAmount
            ), null);
        } catch (Exception e) {
            return new ApiResponse<>(500, "Failed to retrieve rate: " + e.getMessage(), null, e.getMessage());
        }
    }


}
