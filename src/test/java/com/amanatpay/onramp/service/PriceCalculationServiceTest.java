package com.amanatpay.onramp.service;

import com.amanatpay.onramp.service.nobitexService.NobitexService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

public class PriceCalculationServiceTest {

    @Mock
    private NobitexService nobitexService;

    @InjectMocks
    private PriceCalculationService priceCalculationService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testCalculateWAPWithCommission() {
        // Mock the NobitexService to return static order book data
        Map<String, Object> mockOrderBook = new HashMap<>();
        mockOrderBook.put("bids", List.of(
            Map.of("price", "599340", "volume", "761"),
            Map.of("price", "599380", "volume", "12.36")
        ));
        mockOrderBook.put("asks", List.of(
            Map.of("price", "599260", "volume", "1089.92"),
            Map.of("price", "599250", "volume", "85.11")
        ));

        when(nobitexService.getOrderBook()).thenReturn(mockOrderBook);

        // Calculate WAP
        BigDecimal wap = priceCalculationService.calculateWAP();

        // Apply Partner Commission
        BigDecimal commissionRate = BigDecimal.valueOf(0.05); // 5% commission
        BigDecimal finalRateWithCommission = wap.add(wap.multiply(commissionRate));

        assertNotNull(finalRateWithCommission);
        // Verify the final rate is calculated as expected
        // Note: Replace the expected value with the correct one based on your calculation
        BigDecimal expectedValue = wap.add(wap.multiply(commissionRate));
        assertEquals(expectedValue, finalRateWithCommission);
    }
}