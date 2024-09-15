package com.amanatpay.onramp.filter;


import java.util.List;

public class DefaultFilterChain implements FilterChain {
    private final List<Filter> filters;
    private int currentPosition = 0;

    public DefaultFilterChain(List<Filter> filters) {
        this.filters = filters;
    }

    @Override
    public void doFilter(FilterContext context) throws SecurityException {
        if (currentPosition < filters.size()) {
            filters.get(currentPosition++).apply(context, this);
        }
    }
}