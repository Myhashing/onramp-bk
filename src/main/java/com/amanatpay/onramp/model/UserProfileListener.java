package com.amanatpay.onramp.model;

import com.amanatpay.onramp.util.EncryptionUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import jakarta.persistence.PostLoad;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;

@Component
public class UserProfileListener {

    @Autowired
    private EncryptionUtil encryptionUtil;

    @PrePersist
    @PreUpdate
    public void encryptFields(UserProfile userProfile) {
        userProfile.setMobile(encryptionUtil.encrypt(userProfile.getMobile()));
        userProfile.setNationalId(encryptionUtil.encrypt(userProfile.getNationalId()));
    }

    @PostLoad
    public void decryptFields(UserProfile userProfile) {
        userProfile.setMobile(encryptionUtil.decrypt(userProfile.getMobile()));
        userProfile.setNationalId(encryptionUtil.decrypt(userProfile.getNationalId()));
    }
}