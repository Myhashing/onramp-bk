package com.amanatpay.onramp.service;

import com.amanatpay.onramp.dto.BusinessRegistrationRequest;
import com.amanatpay.onramp.entity.Business;
import com.amanatpay.onramp.repository.BusinessRepository;
import io.fusionauth.client.FusionAuthClient;
import io.fusionauth.domain.User;
import io.fusionauth.domain.api.UserRequest;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class BusinessService {

    private final BusinessRepository businessRepository;

    private final FusionAuthClient fusionAuthClient;

    public BusinessService(BusinessRepository businessRepository, FusionAuthClient fusionAuthClient) {
        this.businessRepository = businessRepository;
        this.fusionAuthClient = fusionAuthClient;
    }

    public Business registerBusiness(BusinessRegistrationRequest request) {
        // Create and save the business entity
        Business business = new Business();
        business.setId(UUID.randomUUID());
        business.setBusinessName(request.getBusinessName());
        business.setWebsiteUrl(request.getWebsiteUrl());
        business.setBusinessOwnerId(UUID.randomUUID()); // Temporary ID, will be updated after FusionAuth registration
        Business savedBusiness = businessRepository.save(business);

        // Register the owner in FusionAuth
        User user = new User();
        user.email = request.getOwnerEmail();
        user.password = request.getOwnerPassword();
        user.data.put("userType", "corporate");
        user.data.put("businessId", savedBusiness.getId().toString());

        UserRequest userRequest = new UserRequest(user);
        fusionAuthClient.createUser(null, userRequest);

        // Update the business with the actual owner ID
        savedBusiness.setBusinessOwnerId(user.id);
        businessRepository.save(savedBusiness);

        return savedBusiness;
    }


}