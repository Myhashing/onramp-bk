package com.amanatpay.onramp.filter;

import lombok.Data;

@Data
public class FilterContext {
    private Long businessId;
    private String mobileNumber;
    private String userRole;
    private String ipAddress;
    // Add other necessary fields


}