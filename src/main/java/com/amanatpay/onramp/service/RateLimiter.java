package com.amanatpay.onramp.service;

import java.util.concurrent.ConcurrentHashMap;

public class RateLimiter {
    private final ConcurrentHashMap<String, Long> requestCounts = new ConcurrentHashMap<>();
    private final long maxRequests;
    private final long timeWindowMillis;

    public RateLimiter(long maxRequests, long timeWindowMillis) {
        this.maxRequests = maxRequests;
        this.timeWindowMillis = timeWindowMillis;
    }

    public boolean isRateLimited(String userId) {
        long currentTime = System.currentTimeMillis();
        requestCounts.entrySet().removeIf(entry -> currentTime - entry.getValue() > timeWindowMillis);

        long requestCount = requestCounts.getOrDefault(userId, 0L);
        if (requestCount >= maxRequests) {
            return true;
        }

        requestCounts.put(userId, requestCount + 1);
        return false;
    }
}