package com.amanatpay.onramp.service;

import com.amanatpay.onramp.dto.ApiResponse;
import org.springframework.beans.factory.annotation.Value;
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

    @Value("${auth.url}")
    private String authUrl;

    @Value("${auth.client_id}")
    private String clientId;

    @Value("${auth.client_secret}")
    private String clientSecret;

    @Value("${auth.grant_type}")
    private String grantType;

    @Value("${auth.scope}")
    private String scope;

    public ApiResponse<Map<String, Object>> login(String username, String password) {

        // Create headers
        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/x-www-form-urlencoded");

        // Create body
        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("client_id", clientId);
        body.add("client_secret", clientSecret);
        body.add("grant_type", grantType);
        body.add("username", username);
        body.add("password", password);
        body.add("scope", scope);

        // Create entity
        HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(body, headers);

        // Create RestTemplate instance
        RestTemplate restTemplate = new RestTemplate();

        try {
            // Send request
            ResponseEntity<Map> response = restTemplate.exchange(authUrl, HttpMethod.POST, entity, Map.class);
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