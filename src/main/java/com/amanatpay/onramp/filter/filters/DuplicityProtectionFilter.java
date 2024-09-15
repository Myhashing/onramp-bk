package com.amanatpay.onramp.filter.filters;

import com.amanatpay.onramp.filter.*;
import com.amanatpay.onramp.service.RateBookingService;
import org.springframework.stereotype.Component;

@Component
@FilterCondition(roles = {"CUSTOMER"})
public class DuplicityProtectionFilter implements Filter {

    private RateBookingService rateBookingService;

    public DuplicityProtectionFilter(RateBookingService rateBookingService) {
        this.rateBookingService = rateBookingService;
    }

    @Override
    public void apply(FilterContext context, FilterChain chain) throws SecurityException {
        boolean isDuplicate = rateBookingService.hasUserBookedRateWithMobileNumberAndPartnerUserId(context.getMobileNumber(), context.getBusinessId());

        if (isDuplicate) {
            throw new SecurityException("Duplicate booking detected.");
        }
        chain.doFilter(context);
    }
}