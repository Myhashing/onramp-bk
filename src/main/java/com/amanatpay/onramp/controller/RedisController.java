package com.amanatpay.onramp.controller;

import com.amanatpay.onramp.service.RedisConnectionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RedisController {

    @Autowired
    private RedisConnectionService redisConnectionService;

    @GetMapping("/check-redis-connection")
    public String checkRedisConnection() {
        return redisConnectionService.checkConnection();
    }
}
