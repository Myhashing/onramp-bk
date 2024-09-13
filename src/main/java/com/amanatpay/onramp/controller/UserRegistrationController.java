package com.amanatpay.onramp.controller;

import com.amanatpay.onramp.dto.ApiResponse;
import com.amanatpay.onramp.dto.UserRegistrationRequest;
import com.amanatpay.onramp.dto.VerifyOtpRequest;
import com.amanatpay.onramp.service.UserRegistrationService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
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


}