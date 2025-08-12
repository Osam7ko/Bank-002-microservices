package com.osama.bank002.transaction.config;

import com.osama.bank002.transaction.domain.dto.UserPrincipal;
import feign.RequestInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.context.SecurityContextHolder;

@Configuration
@RequiredArgsConstructor
public class FeignAuthConfig {

    private final ServiceTokenProvider serviceTokenProvider;

    @Bean
    public RequestInterceptor relayAuthToken() {
        return template -> {
            var auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth != null && auth.getPrincipal() instanceof UserPrincipal p && p.token() != null) {
                template.header("Authorization", "Bearer " + p.token());
            } else {
                template.header("Authorization", "Bearer " + serviceTokenProvider.issueServiceToken());
            }
        };
    }
}