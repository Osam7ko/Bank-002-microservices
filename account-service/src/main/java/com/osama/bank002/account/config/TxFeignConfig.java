package com.osama.bank002.account.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;

@Configuration
@RequiredArgsConstructor
public class TxFeignConfig {
    private final ServiceTokenProvider serviceTokenProvider;

    @Bean
    public feign.RequestInterceptor serviceOnlyAuth() {
        return template ->
                template.header(HttpHeaders.AUTHORIZATION, "Bearer " + serviceTokenProvider.issueServiceToken());
    }
}