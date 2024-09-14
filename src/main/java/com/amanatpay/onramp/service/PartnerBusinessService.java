package com.amanatpay.onramp.service;

import com.amanatpay.onramp.repository.PartnerBusinessRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PartnerBusinessService {

    @Autowired
    private PartnerBusinessRepository partnerBusinessRepository;

    public String getBusinessLanguage(Long businessId) {
        return partnerBusinessRepository.findLanguageByBusinessId(businessId)
                .orElse("en"); // Default to English if not set
    }
}

