package com.amanatpay.onramp;

import com.amanatpay.onramp.filter.filters.IpAddressFilter;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
@EnableJpaAuditing
public class OnrampApplication {

    public static void main(String[] args) {
        SpringApplication.run(OnrampApplication.class, args);
    }

    @Bean
    public FilterRegistrationBean<IpAddressFilter> ipAddressFilter() {
        FilterRegistrationBean<IpAddressFilter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(new IpAddressFilter());
        registrationBean.addUrlPatterns("/*");
        return registrationBean;
    }

    @Bean
    public org.springframework.web.filter.CommonsRequestLoggingFilter requestLoggingFilter() {
        org.springframework.web.filter.CommonsRequestLoggingFilter loggingFilter = new org.springframework.web.filter.CommonsRequestLoggingFilter();
        loggingFilter.setIncludeClientInfo(true);
        loggingFilter.setIncludeQueryString(true);
        loggingFilter.setIncludePayload(true);
        loggingFilter.setMaxPayloadLength(10000);
        return loggingFilter;
    }
}