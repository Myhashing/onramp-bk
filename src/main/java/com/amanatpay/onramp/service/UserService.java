package com.amanatpay.onramp.service;

import com.amanatpay.onramp.dto.KycUpdateRequest;
import io.fusionauth.client.FusionAuthClient;
import io.fusionauth.domain.api.UserResponse;
import io.fusionauth.domain.api.UserRequest;
import io.fusionauth.domain.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
public class UserService {

    @Autowired
    private FusionAuthClient fusionAuthClient;

    public void updateKyc(UUID userId, KycUpdateRequest request) {
        UserResponse userResponse = fusionAuthClient.retrieveUser(userId).successResponse;
        User user = userResponse.user;

        if (user.data == null) {
            user.data = new HashMap<>();
        }

        Map<String, Object> kycData = new HashMap<>();
        kycData.put("mobileVerified", request.getMobileVerified());
        kycData.put("emailVerified", request.getEmailVerified());
        kycData.put("addressVerified", request.getAddressVerified());
        kycData.put("kycLevel", request.getKycLevel());
        kycData.put("videoVerified", request.getVideoVerified());
        kycData.put("kycNotes", request.getKycNotes());

        user.data.put("kyc", kycData);

        UserRequest userRequest = new UserRequest(user);
        fusionAuthClient.updateUser(userId, userRequest);
    }
}