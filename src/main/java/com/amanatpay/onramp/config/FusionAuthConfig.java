package com.amanatpay.onramp.config;

import io.fusionauth.client.FusionAuthClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FusionAuthConfig {

    @Value("${fusionauth.api-key}")
    private String API_KEY;

    @Value("${fusionauth.base-url}")
    private String BASE_URL;

    @Bean
    public FusionAuthClient fusionAuthClient() {
        return new FusionAuthClient(API_KEY, BASE_URL);
    }
}