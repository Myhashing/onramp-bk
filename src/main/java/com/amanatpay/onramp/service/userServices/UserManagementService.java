package com.amanatpay.onramp.service.userServices;

import org.springframework.stereotype.Service;

@Service
public class UserManagementService {

    private FusionAuthService fusionAuthService;

    public UserManagementService(FusionAuthService fusionAuthService) {
        this.fusionAuthService = fusionAuthService;
    }





}
