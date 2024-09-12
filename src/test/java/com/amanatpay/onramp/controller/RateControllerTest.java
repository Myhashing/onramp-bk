package com.amanatpay.onramp.controller;

import com.amanatpay.onramp.service.PriceCalculationService;
import com.amanatpay.onramp.service.RateLockService;
import com.amanatpay.onramp.repository.OrderBookDataRepository;
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
import org.springframework.web.context.WebApplicationContext;

import java.math.BigDecimal;
import java.util.Map;

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
    private RateLockService rateLockService;

    @Mock
    private OrderBookDataRepository orderBookDataRepository;

    @InjectMocks
    private RateController rateController;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(rateController).build();
    }


}