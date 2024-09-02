package com.amanatpay.onramp.service;

import com.amanatpay.onramp.dto.ApiResponse;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Service
public class LoginService {

    public ApiResponse<Map<String, Object>> login(String username, String password) {
        String url = "http://localhost:9011/oauth2/token";

        // Create headers
        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/x-www-form-urlencoded");

        // Create body
        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("client_id", "d1f69791-a04e-4e93-8460-7e2242844c53");
        body.add("client_secret", "No7z2JoKT6-0TAoYFZDGEuhNRDkjpWzdOBjtTjrzxZ0");
        body.add("grant_type", "password");
        body.add("username", username);
        body.add("password", password);
        body.add("scope", "openid profile email");

        // Create entity
        HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(body, headers);

        // Create RestTemplate instance
        RestTemplate restTemplate = new RestTemplate();

        try {
            // Send request
            ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.POST, entity, Map.class);
            // Return the full response body
            return new ApiResponse<>(response.getStatusCodeValue(), "Success", response.getBody(), null);
        } catch (HttpClientErrorException e) {
            // Handle client errors (4xx)
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("status", e.getStatusCode().value());
            errorResponse.put("error", e.getStatusText());
            errorResponse.put("message", e.getResponseBodyAsString());
            return new ApiResponse<>(e.getStatusCode().value(), "Client Error", null, e.getResponseBodyAsString());
        } catch (Exception e) {
            // Handle other errors
            return new ApiResponse<>(500, "Internal Server Error", null, e.getMessage());
        }
    }
}