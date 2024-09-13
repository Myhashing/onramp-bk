package com.amanatpay.onramp.service;

import com.amanatpay.onramp.dto.ApiResponse;
import com.amanatpay.onramp.dto.UserRegistrationRequest;
import io.fusionauth.client.FusionAuthClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Service
public class UserRegistrationService {

    private static final Logger logger = LoggerFactory.getLogger(UserRegistrationService.class);
    private final Map<String, Map<String, String>> registrationDataStore = new HashMap<>();
    @Value("${fusionauth.application_id}")
    private String applicationId;
    @Value("${fusionauth.bearer_token}")
    private String bearerToken;
    @Value("${fusionauth.registration_url}")
    private String registrationUrl;
    @Autowired
    private RestTemplate restTemplate;
    @Autowired
    private OtpService otpService;
    @Autowired
    private FusionAuthClient fusionAuthClient;

    public ApiResponse<String> sendOtp(UserRegistrationRequest request, String ipAddress, String userAgent) {
        Objects.requireNonNull(request.getMobilePhone(), "Mobile phone is required");
        Objects.requireNonNull(request.getNationalId(), "National ID is required");
        Objects.requireNonNull(request.getPassword(), "Password is required");

        otpService.sendOtp(request.getMobilePhone(), ipAddress, userAgent);
        // Encrypt and store registration data
        Map<String, String> encryptedData = new HashMap<>();
        encryptedData.put("nationalId", request.getNationalId());
        encryptedData.put("password", request.getPassword());
        encryptedData.put("ipAddress", ipAddress);
        encryptedData.put("userAgent", userAgent);
        encryptedData.put("businessId", request.getBusinessId());
        encryptedData.put("userType", request.getUserType());


        if (request.getEmail() != null) {
            encryptedData.put("email", request.getEmail());
        }
        registrationDataStore.put(request.getMobilePhone(), encryptedData);
        return new ApiResponse<>(200, "OTP Sent", null, null);
    }

    public ApiResponse<Map<String, Object>> registerUser(String mobilePhone, String otp) {
        Map<String, String> encryptedData = registrationDataStore.get(mobilePhone);
        if (encryptedData == null || !otpService.verifyOtp(mobilePhone, otp, encryptedData.get("ipAddress"), encryptedData.get("userAgent"))) {
            return new ApiResponse<>(400, "Invalid OTP or registration data not found", null, null);
        }

        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/json");
        headers.set("Authorization", bearerToken);

        Map<String, Object> user = new HashMap<>();
        user.put("password", encryptedData.get("password"));
        user.put("username", encryptedData.get("nationalId"));
        user.put("mobilePhone", mobilePhone);
        user.put("businessId", encryptedData.get("businessId"));
        user.put("userType", encryptedData.get("userType"));
        user.put("email", encryptedData.get("email")); // Optional field

        // Set default KYC values
        Map<String, Object> kycData = new HashMap<>();
        kycData.put("mobileVerified", true);
        kycData.put("emailVerified", false);
        kycData.put("addressVerified", false);
        kycData.put("kycLevel", 0);
        kycData.put("videoVerified", false);
        kycData.put("kycNotes", null);

        Map<String, Object> data = new HashMap<>();
        data.put("kyc", kycData);
        user.put("data", data);

        Map<String, Object> registration = new HashMap<>();
        registration.put("applicationId", applicationId);

        Map<String, Object> body = new HashMap<>();
        body.put("user", user);
        body.put("registration", registration);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);
        try {
            ResponseEntity<Map> response = restTemplate.exchange(registrationUrl, HttpMethod.POST, entity, Map.class);

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                return new ApiResponse<>(200, "Success", response.getBody(), null);
            } else {
                return new ApiResponse<>(response.getStatusCodeValue(), "Error Registering User", null, response.getBody().toString());
            }
        } catch (HttpClientErrorException e) {
            throw e; // Let the global exception handler handle this
        } catch (Exception e) {
            // Log the exception
            return new ApiResponse<>(500, "Internal Server Error", null, e.getMessage());
        }
    }


/*
    public ApiResponse<Map> registerSubUser(SubUserRegistrationRequest request) {
        // Register the sub-user in FusionAuth
        Map<String, Object> user = new HashMap<>();
        user.put("password", request.getUserPassword());
        user.put("username", request.getUserName());
        user.put("mobilePhone", request.getMobilePhone());
        user.put("businessId", request.getBusinessId());
        user.put("userType", "corporate-sub-user");
        if (request.getUserEmail() != null) {
            user.put("email", request.getUserEmail());
        }
        if ("full-access".equals(request.getAccessLevel())) {
            user.put("roles", new String[]{"CORPORATE_ADMIN"});
        } else {
            user.put("roles", new String[]{"CORPORATE_USER"});
        }

        // Set default KYC values
        Map<String, Object> kycData = new HashMap<>();
        kycData.put("mobileVerified", false);
        kycData.put("emailVerified", false);
        kycData.put("addressVerified", false);
        kycData.put("kycLevel", 0);
        kycData.put("videoVerified", false);
        kycData.put("kycNotes", null);

        Map<String, Object> data = new HashMap<>();
        data.put("kyc", kycData);
        user.put("data", data);

        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/json");
        headers.set("Authorization", bearerToken);
        Map<String, Object> registration = new HashMap<>();
        registration.put("applicationId", applicationId);
        Map<String, Object> body = new HashMap<>();
        body.put("user", user);
        body.put("registration", registration);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);
        try {
            ResponseEntity<Map> response = restTemplate.exchange(registrationUrl, HttpMethod.POST, entity, Map.class);

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                return new ApiResponse<>(200, "Success", response.getBody(), null);
            } else {
                return new ApiResponse<>(response.getStatusCodeValue(), "Error Registering User", null, response.getBody().toString());
            }
        } catch (HttpClientErrorException e) {
            throw e; // Let the global exception handler handle this
        } catch (Exception e) {
            // Log the exception
            return new ApiResponse<>(500, "Internal Server Error", null, e.getMessage());
        }

    }
*/
}