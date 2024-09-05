package com.amanatpay.onramp.controller;

import com.amanatpay.onramp.dto.SubUserRegistrationRequest;
import com.amanatpay.onramp.dto.UserRegistrationRequest;
import com.amanatpay.onramp.dto.ApiResponse;
import com.amanatpay.onramp.dto.VerifyOtpRequest;
import com.amanatpay.onramp.service.UserRegistrationService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/register")
public class UserRegistrationController {

    @Autowired
    private UserRegistrationService userRegistrationService;

    @PostMapping("/otp")
    public ApiResponse<String> generateOtp(@Valid @RequestBody UserRegistrationRequest request,
                                           @RequestHeader("X-Forwarded-User-Agent") String userAgent,
                                           @RequestHeader("X-Forwarded-For") String ipAddress) {
        return userRegistrationService.sendOtp(request, ipAddress, userAgent);
    }

    @PostMapping("/verify")
    public ApiResponse<Map<String, Object>> verifyOtp(@Valid @RequestBody VerifyOtpRequest request) {
        return userRegistrationService.registerUser(request.getMobilePhone(), request.getOtp());
    }

    @PostMapping("/register-sub-user")
    public ResponseEntity<String> registerSubUser(@Valid @RequestBody SubUserRegistrationRequest request) {
        userRegistrationService.registerSubUser(request);
        return ResponseEntity.ok("Sub-user registered successfully");
    }
}