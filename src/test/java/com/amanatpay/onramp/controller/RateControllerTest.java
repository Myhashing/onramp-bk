package com.amanatpay.onramp.controller;


import com.amanatpay.onramp.service.PriceCalculationService;
import com.amanatpay.onramp.service.RateBookingService;
import com.amanatpay.onramp.service.RedisService;
import com.amanatpay.onramp.service.SpreadService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class RateControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Mock
    private PriceCalculationService priceCalculationService;

    @Mock
    private SpreadService spreadService;

    @Mock
    private RedisService redisService;

    @Mock
    private RateBookingService rateBookingService;

    @InjectMocks
    private RateController rateController;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(rateController).build();
    }

    @Test
    void getRateSuccessfully() throws Exception {
        Long businessId = 1L;
        BigDecimal finalRate = new BigDecimal("1.5");

        when(priceCalculationService.calculateWAP()).thenReturn(new BigDecimal("1.0"));
        when(spreadService.applyDynamicSpread(new BigDecimal("1.0"))).thenReturn(new BigDecimal("1.2"));
        when(priceCalculationService.applyCommission(new BigDecimal("1.2"), businessId)).thenReturn(finalRate);

        String result = mockMvc.perform(get("/rate")
                .param("businessId", "1"))
        .andReturn()
        .getResponse()
        .getContentAsString();

System.out.println(result);  // Inspect the result here

    }

    @Test
    void getRateFailsDueToException() throws Exception {
        Long businessId = 1L;

        when(priceCalculationService.calculateWAP()).thenThrow(new RuntimeException("Calculation error"));

        mockMvc.perform(get("/rate")
                        .param("businessId", businessId.toString()))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.status").value(500))
                .andExpect(jsonPath("$.message").value("Failed to retrieve rate: Calculation error"));
    }

    @Test
    void getRateWithMissingParameter() throws Exception {
        mockMvc.perform(get("/rate"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").value("Required request parameter 'businessId' for method parameter type Long is not present"));
    }
}