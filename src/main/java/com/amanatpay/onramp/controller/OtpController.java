package com.amanatpay.onramp.controller;

import com.amanatpay.onramp.dto.ApiResponse;
import com.amanatpay.onramp.service.AuthService;
import com.amanatpay.onramp.service.OtpService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
public class OtpController {

    @Autowired
    private OtpService otpService;

    @Autowired
    private AuthService authService;

    @PostMapping("/sendOtp")
    public ApiResponse<Map<String, Object>> sendOtp(@RequestBody Map<String, String> requestBody,
                                                    @RequestHeader("X-Forwarded-User-Agent") String userAgent,
                                                    @RequestHeader("X-Forwarded-For") String ipAddress) {
        try {
            String mobileNumber = requestBody.get("mobileNumber");
            return otpService.sendOtp(mobileNumber, ipAddress, userAgent);
        } catch (Exception e) {
            return ApiResponse.createErrorResponse(500, "Internal Server Error", e.getMessage());
        }
    }

    @PostMapping("/verifyOtp")
    public ApiResponse<Map<String, Object>> verifyOtp(@RequestBody Map<String, String> requestBody,
                                                      @RequestHeader("X-Forwarded-User-Agent") String userAgent,
                                                      @RequestHeader("X-Forwarded-For") String ipAddress) {
        try {
            String mobileNumber = requestBody.get("mobileNumber");
            String otpCode = requestBody.get("otpCode");
            if (otpService.verifyOtp(mobileNumber, otpCode, ipAddress, userAgent)) {
                return authService.authenticate(mobileNumber, ipAddress, userAgent);
            } else {
                return ApiResponse.createErrorResponse(400, "Invalid OTP", "The OTP code is incorrect or the request is from a different device.");
            }
        } catch (Exception e) {
            return ApiResponse.createErrorResponse(500, "Internal Server Error", e.getMessage());
        }
    }
}