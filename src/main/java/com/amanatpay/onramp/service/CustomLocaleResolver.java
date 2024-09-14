package com.amanatpay.onramp.service;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;

import java.util.Locale;

@Service
public class CustomLocaleResolver extends SessionLocaleResolver {

/*
    @Autowired
    private UserService userService;  // To get user language from FusionAuth
*/

    @Autowired
    private PartnerBusinessService partnerBusinessService;  // To get business language from DB

    @Override
    public Locale resolveLocale(HttpServletRequest request) {
        // Check if the 'lang' parameter is passed in the request
        String langParam = request.getParameter("lang");

        if (langParam != null && !langParam.isEmpty()) {
            // Use the language passed in the request
            return new Locale(langParam);
        }

/*        // Get user ID from authentication (FusionAuth or session)
        String userId = getAuthenticatedUserId();
        if (userId != null) {
            // Get user language from FusionAuth
            String userLanguage = userService.getUserPreferredLanguage(userId);
            if (userLanguage != null && !userLanguage.isEmpty()) {
                return new Locale(userLanguage);
            }
        }*/

        // Get business ID from the request or session
        Long businessId = getBusinessIdFromRequestOrSession(request);
        if (businessId != null) {
            // Get business language from partner_business table
            String businessLanguage = partnerBusinessService.getBusinessLanguage(businessId);
            if (businessLanguage != null && !businessLanguage.isEmpty()) {
                return new Locale(businessLanguage);
            }
        }

        // Default to English if no language setting is found
        return Locale.ENGLISH;
    }

    // Helper method to retrieve the authenticated user's ID
    private String getAuthenticatedUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            return authentication.getName();  // Assumes the username is the userId
        }
        return null;
    }

    // Helper method to retrieve business ID from request or session
    private Long getBusinessIdFromRequestOrSession(HttpServletRequest request) {
        // Extract business ID from request or session attributes
        Object businessIdAttr = request.getSession().getAttribute("businessId");
        if (businessIdAttr != null) {
            return (Long) businessIdAttr;
        }
        // Optionally, you can check for businessId in request parameters or headers
        return null;
    }
}

