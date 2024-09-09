package com.amanatpay.onramp;

import com.amanatpay.onramp.filter.IpAddressFilter;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
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
}