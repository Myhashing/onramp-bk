package com.amanatpay.onramp.service;

import com.amanatpay.onramp.dto.ApiResponse;
import com.amanatpay.onramp.repository.UserProfileRepository;
import com.amanatpay.onramp.util.EncryptionUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

@Service
public class AuthService {

    @Value("${fusionauth.application_id}")
    private String applicationId;

    @Value("${fusionauth.passwordless.url}")
    private String startUrl;

    @Value("${fusionauth.passwordless.login_url}")
    private String loginUrl;

    @Value("${fusionauth.search_url}")
    private String searchUrl;

    @Value("${fusionauth.bearer_token}")
    private String bearerToken;

    @Autowired
    private UserProfileRepository userProfileRepository;

    @Autowired
    private EncryptionUtil encryptionUtil;

    public ApiResponse<Map<String, Object>> authenticate(String mobileNumber, String ipAddress, String userAgent) {
        // Search for the user by mobile number
        HttpHeaders searchHeaders = new HttpHeaders();
        searchHeaders.set("Content-Type", "application/json");
        searchHeaders.set("Authorization", bearerToken);

        Map<String, Object> searchBody = new HashMap<>();
        Map<String, String> searchQuery = new HashMap<>();
        searchQuery.put("queryString", "mobilePhone:" + mobileNumber);
        searchBody.put("search", searchQuery);

        HttpEntity<Map<String, Object>> searchEntity = new HttpEntity<>(searchBody, searchHeaders);
        RestTemplate restTemplate = new RestTemplate();

        try {
            ResponseEntity<Map> searchResponse = restTemplate.exchange(searchUrl, HttpMethod.POST, searchEntity, Map.class);
            if (searchResponse.getStatusCode().value() != 200 || searchResponse.getBody() == null || ((Map) searchResponse.getBody()).get("users") == null) {
                return new ApiResponse<>(searchResponse.getStatusCode().value(), "Error Searching User", null, searchResponse.getBody().toString());
            }

            Map user = ((List<Map>) ((Map) searchResponse.getBody()).get("users")).get(0);
            String userId = (String) user.get("username");

            // Start passwordless login
            HttpHeaders startHeaders = new HttpHeaders();
            startHeaders.set("Content-Type", "application/json");
            startHeaders.set("Authorization", bearerToken);

            Map<String, Object> startBody = new HashMap<>();
            startBody.put("loginId", userId);
            startBody.put("applicationId", UUID.fromString(applicationId)); // Ensure applicationId is a valid UUID

            HttpEntity<Map<String, Object>> startEntity = new HttpEntity<>(startBody, startHeaders);
            ResponseEntity<Map> startResponse = restTemplate.exchange(startUrl, HttpMethod.POST, startEntity, Map.class);
            if (startResponse.getStatusCode().value() != 200) {
                return new ApiResponse<>(startResponse.getStatusCode().value(), "Error Starting Passwordless Login", null, startResponse.getBody().toString());
            }

            // Complete passwordless login

            HttpHeaders loginHeaders = new HttpHeaders();
            loginHeaders.set("Content-Type", "application/json");
            loginHeaders.set("Authorization", bearerToken);

            Map<String, String> loginBody = new HashMap<>();
            loginBody.put("code", Objects.requireNonNull(startResponse.getBody()).get("code").toString());
            loginBody.put("applicationId", applicationId);

            HttpEntity<Map<String, String>> loginEntity = new HttpEntity<>(loginBody, loginHeaders);
            ResponseEntity<Map> loginResponse = restTemplate.exchange(loginUrl, HttpMethod.POST, loginEntity, Map.class);

            int statusCode = loginResponse.getStatusCode().value();
            if ((statusCode >= 200 && statusCode < 300) && loginResponse.getBody() != null) {
                Map<String, Object> responseData = new HashMap<>();
                responseData.put("token", loginResponse.getBody().get("token"));
                responseData.put("refreshToken", loginResponse.getBody().get("refreshToken"));
                responseData.put("user", loginResponse.getBody().get("user"));
                return new ApiResponse<>(200, "Success", responseData, null);
            } else {
                return new ApiResponse<>(statusCode, "Error Completing Passwordless Login", null, loginResponse.getBody().toString());
            }
        } catch (Exception e) {
            return new ApiResponse<>(500, "Internal Server Error", null, e.getMessage());
        }
    }
}