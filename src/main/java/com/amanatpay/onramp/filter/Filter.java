package com.amanatpay.onramp.filter;

public interface Filter {
    void apply(FilterContext context, FilterChain chain) throws SecurityException;
}