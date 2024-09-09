package com.amanatpay.onramp.controller;

import com.amanatpay.onramp.dto.ApiResponse;
import com.amanatpay.onramp.entity.OrderBookData;
import com.amanatpay.onramp.service.PriceCalculationService;
import com.amanatpay.onramp.service.RateLockService;
import com.amanatpay.onramp.repository.OrderBookDataRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/rate")
public class RateController {

    @Autowired
    private PriceCalculationService priceCalculationService;

    @Autowired
    private RateLockService rateLockService;

    @Autowired
    private OrderBookDataRepository orderBookDataRepository;

    @GetMapping
    public ApiResponse<Map<String ,BigDecimal>> getRate() {
        BigDecimal wap = priceCalculationService.calculateWAP();
        BigDecimal rateWithSpread = priceCalculationService.applyDynamicSpread(wap);
        BigDecimal lockedRate = rateLockService.getLockedRate("USDTIRT", rateWithSpread);

        // Save data to the database
        OrderBookData orderBookData = new OrderBookData();
        orderBookData.setId(UUID.randomUUID());
        orderBookData.setWap(wap);
        orderBookData.setSpread(rateWithSpread.subtract(wap));
        orderBookData.setTimestamp(new Timestamp(System.currentTimeMillis()));
        orderBookDataRepository.save(orderBookData);

        return new ApiResponse<>(200, "OK", Map.of("rate", lockedRate), null);
    }
}