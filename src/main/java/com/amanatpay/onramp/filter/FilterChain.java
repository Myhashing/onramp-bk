package com.amanatpay.onramp.filter;


public interface FilterChain {
    void doFilter(FilterContext context) throws SecurityException;
}
