package com.amanatpay.onramp.controller;

import com.amanatpay.onramp.dto.BusinessRegistrationRequest;
import com.amanatpay.onramp.entity.Business;
import com.amanatpay.onramp.service.BusinessService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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