package com.amanatpay.onramp.dto;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class RateBooking implements Serializable {

    private String bookingId;
    private Long partnerUserId;
    private BigDecimal rate;
    private BigDecimal amount;
    private LocalDateTime expirationTime;
    private String mobileNumber;
    private Long businessId;

    // Constructor
    public RateBooking(String bookingId, Long partnerUserId, BigDecimal rate, BigDecimal amount, LocalDateTime expirationTime) {
        this.bookingId = bookingId;
        this.partnerUserId = partnerUserId;
        this.rate = rate;
        this.amount = amount;
        this.expirationTime = expirationTime;
    }

    // Getters and Setters
}

