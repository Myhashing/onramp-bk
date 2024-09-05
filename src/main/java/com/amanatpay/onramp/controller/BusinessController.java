package com.amanatpay.onramp.controller;

import com.amanatpay.onramp.dto.BusinessRegistrationRequest;
import com.amanatpay.onramp.entity.Business;
import com.amanatpay.onramp.service.BusinessService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/businesses")
public class BusinessController {

    @Autowired
    private BusinessService businessService;

    @PostMapping("/register")
    public ResponseEntity<Business> registerBusiness(@Valid @RequestBody BusinessRegistrationRequest request) {
        Business registeredBusiness = businessService.registerBusiness(request);
        return ResponseEntity.ok(registeredBusiness);
    }
}