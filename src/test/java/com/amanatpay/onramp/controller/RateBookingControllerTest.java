package com.amanatpay.onramp.controller;

import com.amanatpay.onramp.service.RateBookingService;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class RateBookingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Mock
    private RateBookingService rateBookingService;

    @InjectMocks
    private RateBookingController rateBookingController;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(rateBookingController).build();
    }

    @Test
    public void testConfirmRate() throws Exception {
        String rateBookingId = "testBookingId";
        BigDecimal rate = BigDecimal.valueOf(1000);
        BigDecimal amount = BigDecimal.valueOf(1);
        String userId = "user123";
        String businessId = "business123";

        when(rateBookingService.bookRate(rate, amount, userId, businessId)).thenReturn(rateBookingId);

        mockMvc.perform(post("/api/rate-booking/confirm")
                .param("rate", rate.toString())
                .param("amount", amount.toString())
                .param("userId", userId)
                .param("businessId", businessId))
                .andExpect(status().isFound())
                .andExpect(header().string("Location", "https://onramp-frontend.com/payment?orderId=" + rateBookingId +
                                "&rate=" + rate +
                                "&amount=" + amount +
                                "&businessId=" + businessId));
    }
}