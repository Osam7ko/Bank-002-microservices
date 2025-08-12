package com.osama.bank002.profile.config;

import feign.RequestInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Configuration
@RequiredArgsConstructor
public class FeignAuthConfig {
    private final ServiceTokenProvider serviceTokenProvider;

    @Bean
    public RequestInterceptor relayAuthToken() {
        return template -> {
            var attrs = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            String authHeader = (attrs != null)
                    ? attrs.getRequest().getHeader(HttpHeaders.AUTHORIZATION)
                    : null;

            if (org.springframework.util.StringUtils.hasText(authHeader)) {
                template.header(HttpHeaders.AUTHORIZATION, authHeader);
            } else {
                template.header(HttpHeaders.AUTHORIZATION, "Bearer " + serviceTokenProvider.issueServiceToken());
            }
        };
    }
}