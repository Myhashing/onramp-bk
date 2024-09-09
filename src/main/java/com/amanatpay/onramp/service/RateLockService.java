package com.amanatpay.onramp.service;

import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class RateLockService {

    private final ConcurrentHashMap<String, RateLock> rateLocks = new ConcurrentHashMap<>();

    public BigDecimal getLockedRate(String key, BigDecimal rate) {
        RateLock rateLock = rateLocks.get(key);
        if (rateLock == null || rateLock.getExpiryTime().isBefore(LocalDateTime.now())) {
            rateLock = new RateLock(rate, LocalDateTime.now().plusMinutes(5));
            rateLocks.put(key, rateLock);
        }
        return rateLock.getRate();
    }

    private static class RateLock {
        private final BigDecimal rate;
        private final LocalDateTime expiryTime;

        public RateLock(BigDecimal rate, LocalDateTime expiryTime) {
            this.rate = rate;
            this.expiryTime = expiryTime;
        }

        public BigDecimal getRate() {
            return rate;
        }

        public LocalDateTime getExpiryTime() {
            return expiryTime;
        }
    }
}