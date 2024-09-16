package com.amanatpay.onramp.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
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


    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime expirationTime;
    private String mobileNumber;
    private Long businessId;
    private String walletAddress;
    private String nationalCode;

    // Constructor
    public RateBooking() {}


    // Getters and Setters
}


