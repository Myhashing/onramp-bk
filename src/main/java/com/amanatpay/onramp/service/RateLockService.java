package com.amanatpay.onramp.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.concurrent.TimeUnit;

@Service
public class RateLockService {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    private static final long RATE_LOCK_DURATION = 5; // 5 minutes

    public void lockRate(String rateBookingId, BigDecimal rate) {
        redisTemplate.opsForValue().set(rateBookingId, rate, RATE_LOCK_DURATION, TimeUnit.MINUTES);
    }

    public BigDecimal getLockedRate(String rateBookingId) {
        return (BigDecimal) redisTemplate.opsForValue().get(rateBookingId);
    }

    public boolean isRateLocked(String rateBookingId) {
        return redisTemplate.hasKey(rateBookingId);
    }
}