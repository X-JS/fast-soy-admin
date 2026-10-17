package com.fastsoy.admin.config;

import com.fastsoy.admin.dao.UserMapper;
import com.fastsoy.admin.filter.JwtAuthFilter;
import com.fastsoy.common.jwt.JwtUtils;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FilterConfig {

    @Bean
    public FilterRegistrationBean<JwtAuthFilter> jwtAuthFilterRegistration(JwtUtils jwtUtils, UserMapper userMapper) {
        FilterRegistrationBean<JwtAuthFilter> registration = new FilterRegistrationBean<>();
        registration.setFilter(new JwtAuthFilter(jwtUtils, userMapper));
        registration.addUrlPatterns("/*");
        registration.setOrder(Integer.MIN_VALUE);
        registration.setName("jwtAuthFilter");
        return registration;
    }
}
