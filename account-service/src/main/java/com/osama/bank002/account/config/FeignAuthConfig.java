package com.osama.bank002.account.config;

import feign.RequestInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Configuration
public class FeignAuthConfig {
    @Bean
    public RequestInterceptor relayAuthToken() {
        return template -> {
            var attrs = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attrs == null) return;
            var hdr = attrs.getRequest().getHeader(HttpHeaders.AUTHORIZATION);
            if (org.springframework.util.StringUtils.hasText(hdr)) {
                template.header(HttpHeaders.AUTHORIZATION, hdr);
            }
        };
    }
}