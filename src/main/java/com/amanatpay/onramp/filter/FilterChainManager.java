package com.amanatpay.onramp.filter;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class FilterChainManager {
    private final List<Filter> filters;

    public FilterChainManager(List<Filter> filters) {
        this.filters = filters;
    }

    public FilterChain createFilterChain(FilterContext context) {
        List<Filter> applicableFilters = new ArrayList<>();
        for (Filter filter : filters) {
            FilterCondition condition = filter.getClass().getAnnotation(FilterCondition.class);
            if (condition != null) {
                for (String role : condition.roles()) {
                    if (role.trim().equalsIgnoreCase(context.getUserRole().trim())) {
                        applicableFilters.add(filter);
                        break;
                    }
                }
            } else {
                applicableFilters.add(filter);
            }
        }
        if (applicableFilters.isEmpty()) {
            System.out.println("No applicable filters found for user role: " + context.getUserRole());
        } else {
            System.out.println("Applicable filters: " + applicableFilters);
        }
        return new DefaultFilterChain(applicableFilters);
    }
}