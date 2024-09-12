package com.amanatpay.onramp.controller;

import com.amanatpay.onramp.service.RateBookingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping("/api/rate-booking")
public class RateBookingController {

    @Autowired
    private RateBookingService rateBookingService;

    @PostMapping("/confirm")
    public ResponseEntity<String> confirmRate(
            @RequestParam("rate") BigDecimal rate,
            @RequestParam("amount") BigDecimal amount,
            @RequestParam("userId") String userId,
            @RequestParam("businessId") String businessId) {

        // Generate Rate Booking ID and save the details
        String rateBookingId = rateBookingService.bookRate(rate, amount, userId, businessId);

        // Generate the redirection URL
        String redirectionUrl = "https://onramp-frontend.com/payment?orderId=" + rateBookingId +
                                "&rate=" + rate +
                                "&amount=" + amount +
                                "&businessId=" + businessId;

        // Redirect the user to the frontend
        return ResponseEntity.status(HttpStatus.FOUND).header("Location", redirectionUrl).build();
    }
}