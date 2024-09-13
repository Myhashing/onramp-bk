package com.amanatpay.onramp.controller;

import com.amanatpay.onramp.dto.KycUpdateRequest;
import com.amanatpay.onramp.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserService userService;

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{userId}/kyc")
    public ResponseEntity<String> updateKyc(@PathVariable UUID userId, @Valid @RequestBody KycUpdateRequest request) {
        userService.updateKyc(userId, request);
        return ResponseEntity.ok("KYC details updated successfully");
    }
}