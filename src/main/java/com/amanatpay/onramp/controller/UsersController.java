package com.amanatpay.onramp.controller;

import com.amanatpay.onramp.dto.*;
import com.amanatpay.onramp.service.userServices.FusionAuthService;
import com.amanatpay.onramp.service.userServices.UserManagementService;
import com.amanatpay.onramp.service.userServices.UserRegistrationService;
import com.inversoft.error.Errors;
import com.inversoft.rest.ClientResponse;
import io.fusionauth.domain.User;
import io.fusionauth.domain.api.UserRequest;
import io.fusionauth.domain.api.UserResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/users")
public class UsersController {

    private final UserManagementService userService;
    private final FusionAuthService fusionAuthService;
    private final UserRegistrationService userRegistrationService;

    public UsersController(UserManagementService userService, FusionAuthService fusionAuthService, UserRegistrationService userRegistrationService) {
        this.userService = userService;
        this.fusionAuthService = fusionAuthService;
        this.userRegistrationService = userRegistrationService;
    }

    @GetMapping("/check")
    public ApiResponse<UserExistenceResponse> checkUserExistence(
            @RequestParam(required = false) String mobileNumber,
            @RequestParam(required = false) String nationalCode) {
        boolean exists = false;
        UUID userId = null;

        try {
            //TODO: validation of mobileNumber and nationalCode format
            if (mobileNumber != null && nationalCode != null) {
                User user = fusionAuthService.searchUserByUsername(nationalCode);
                if (user != null && user.mobilePhone.equals(mobileNumber)) {
                    exists = true;
                    userId = user.id;

                }
            }
        } catch (Exception e) {
            // Handle exceptions
            return new ApiResponse<>(500, "Failed to check user existence: " + e.getMessage(), null, null);
        }
        UserExistenceResponse response = new UserExistenceResponse(exists, userId);
        return new ApiResponse<>(200, "User existence check completed", response, null);
    }

    @GetMapping("/{userId}/kyc-status")
    public ApiResponse<KycStatusResponse> getKycStatus(@PathVariable UUID userId) {
        try {
            // Retrieve the user from FusionAuth
            User user = fusionAuthService.retrieveUserById(userId);

            // Extract the KYC level from the user's custom data
            int kycLevel = user.data.get("kycLevel") != null ? (int) user.data.get("kycLevel") : 0;
            String statusMessage = kycLevel > 3 ? "KYC level is sufficient" : "KYC level is insufficient";

            // Create the response object
            KycStatusResponse response = new KycStatusResponse(kycLevel, statusMessage);

            // Return the response
            return new ApiResponse<>(200, "KYC status retrieved successfully", response, null);
        } catch (Exception e) {
            // Handle exceptions and return an error response
            return new ApiResponse<>(500, "Failed to retrieve KYC status: " + e.getMessage(), null, null);
        }
    }

    @PostMapping("/create")
    public ResponseEntity<UserCreationResponse> createUser(
            @RequestParam String mobileNumber,
            @RequestParam String nationalCode,
            @RequestBody AdditionalUserData additionalData) {
        try {
            // Create a new user object
            User user = new User();
            user.mobilePhone = mobileNumber;
            user.username = nationalCode;
            user.data = additionalData.toMap();

            // Create the user in FusionAuth
            UserRequest request = new UserRequest(user);

            ClientResponse<UserResponse, Errors> response = fusionAuthService.createUser(request);

            if (response.wasSuccessful()) {
                // Return a success response
                User createdUser = response.successResponse.user;
                UserCreationResponse userCreationResponse = new UserCreationResponse(createdUser.id, "User created successfully");
                return ResponseEntity.ok(userCreationResponse);
            } else {
                // Handle errors
                String errorMessage = response.errorResponse != null ? response.errorResponse.toString() : "Unknown error";
                return ResponseEntity.status(500).body(new UserCreationResponse(null, "Failed to create user: " + errorMessage));
            }
        } catch (Exception e) {
            // Handle exceptions
            return ResponseEntity.status(500).body(new UserCreationResponse(null, "Failed to create user: " + e.getMessage()));
        }
    }

    //send otp to created user
    @PostMapping("/send-otp")
    public ApiResponse<String> sendOtp(@RequestParam UUID userId,
                                       @RequestHeader("X-Forwarded-User-Agent") String userAgent,
                                       @RequestHeader("X-Forwarded-For") String ipAddress) {
        try {
            // Send OTP to the user
            boolean otpSent = userRegistrationService.sendOtp(userId, ipAddress, userAgent);
            if (otpSent) {
                // Return a success response
                return new ApiResponse<>(200, "OTP sent successfully", "OTP sent successfully", null);
            } else {
                // Return an error response
                return new ApiResponse<>(500, "Failed to send OTP", null, null);
            }
        } catch (Exception e) {
            // Handle exceptions
            return new ApiResponse<>(500, "Failed to send OTP: " + e.getMessage(), null, null);
        }
    }

    @PostMapping("/verify-otp")
    public ApiResponse<String> verifyOtp(@RequestParam UUID userId,
                                         @RequestParam String otp,
                                         @RequestHeader("X-Forwarded-User-Agent") String userAgent,
                                         @RequestHeader("X-Forwarded-For") String ipAddress) {
        try {
            // Verify the OTP
            boolean otpVerified = userRegistrationService.verifyOtp(userId, otp, ipAddress, userAgent);
            if (otpVerified) {
                userRegistrationService.verifyUserOTPForKYC(userId);
                // Return a success response
                return new ApiResponse<>(200, "OTP verified successfully", "OTP verified successfully", null);
            } else {
                // Return an error response
                return new ApiResponse<>(400, "Invalid OTP", null, null);
            }
        } catch (Exception e) {
            // Handle exceptions
            return new ApiResponse<>(500, "Failed to verify OTP: " + e.getMessage(), null, null);
        }
    }
}