package com.amanatpay.onramp.service.userServices;

import com.amanatpay.onramp.dto.ApiResponse;
import com.amanatpay.onramp.dto.UserRegistrationRequest;
import com.amanatpay.onramp.exception.UserNotFoundException;
import io.fusionauth.domain.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import java.util.UUID;

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
    private final RestTemplate restTemplate;
    private final OtpService otpService;
    private final FusionAuthService fusionAuthService;

    public UserRegistrationService(RestTemplate restTemplate, OtpService otpService, FusionAuthService fusionAuthService) {
        this.restTemplate = restTemplate;
        this.otpService = otpService;
        this.fusionAuthService = fusionAuthService;
    }

    public ApiResponse<String> sendOtpForUserRegister(UserRegistrationRequest request, String ipAddress, String userAgent) {
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

    public Boolean sendOtp(UUID userId, String ipAddress, String userAgent) throws UserNotFoundException {
        User user = fusionAuthService.retrieveUserById(userId);
        if (user == null) {
            throw new UserNotFoundException("User not found");
        }
        String mobileNumber = user.mobilePhone;
        otpService.sendOtp(mobileNumber, ipAddress, userAgent);
        return true;
    }

    public boolean verifyOtp(UUID userId, String otp, String ipAddress, String userAgent) throws UserNotFoundException {
        User user = fusionAuthService.retrieveUserById(userId);
        if (user == null) {
            throw new UserNotFoundException("User not found");
        }
        String mobileNumber = user.mobilePhone;
        return otpService.verifyOtp(mobileNumber, otp, ipAddress, userAgent);
    }

    public boolean verifyUserOTPForKYC(UUID userId) throws UserNotFoundException {
        User user = fusionAuthService.retrieveUserById(userId);
        if (user == null) {
            throw new UserNotFoundException("User not found");
        }
        user.verified = true;
        if (user.data.get("kycLevel") == null || (int) user.data.get("kycLevel") < 1) {
            user.data.put("kycLevel", 1);
            fusionAuthService.updateUser(user);
        }
        return true;
    }
}