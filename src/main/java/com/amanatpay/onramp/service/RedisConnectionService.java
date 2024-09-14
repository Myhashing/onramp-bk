package com.amanatpay.onramp.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class RedisConnectionService {

    private final RedisTemplate<String, String> redisTemplate;

    public RedisConnectionService(RedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public String checkConnection() {
        try {
            // Set a key in Redis to test the connection
            redisTemplate.opsForValue().set("connectionTestKey", "connected");
            // Retrieve the key from Redis to ensure it's working
            return redisTemplate.opsForValue().get("connectionTestKey");
        } catch (Exception e) {
            // Return the error message if there's a connection failure
            return "Redis connection failed: " + e.getMessage();
        }
    }
}
