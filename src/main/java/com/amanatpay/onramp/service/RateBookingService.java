package com.amanatpay.onramp.service;

import com.amanatpay.onramp.entity.RateBooking;
import com.amanatpay.onramp.repository.RateBookingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.UUID;

@Service
public class RateBookingService {

    @Autowired
    private RateBookingRepository rateBookingRepository;

    public String bookRate(BigDecimal rate, BigDecimal amount, String userId, String businessId) {
        String rateBookingId = UUID.randomUUID().toString();

        RateBooking rateBooking = new RateBooking();
        rateBooking.setRateBookingId(rateBookingId);
        rateBooking.setRate(rate);
        rateBooking.setAmount(amount);
        rateBooking.setUserId(userId);
        rateBooking.setBusinessId(businessId);

        rateBookingRepository.save(rateBooking);

        return rateBookingId;
    }
}