package com.osama.bank002.account.config;

import feign.RequestInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

//@Configuration
//public class FeignAuthConfig {
//    @Bean
//    public RequestInterceptor relayAuthToken() {
//        return template -> {
//            var attrs = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
//            if (attrs == null) return;
//            var hdr = attrs.getRequest().getHeader(HttpHeaders.AUTHORIZATION);
//            if (org.springframework.util.StringUtils.hasText(hdr)) {
//                template.header(HttpHeaders.AUTHORIZATION, hdr);
//            }
//        };
//    }
//    @Bean
//    public feign.RequestInterceptor forwardAuth() {
//        return template -> {
//            var attrs = RequestContextHolder.getRequestAttributes();
//            if (attrs instanceof ServletRequestAttributes sra) {
//                String auth = sra.getRequest().getHeader("Authorization");
//                if (auth != null && !auth.isBlank()) {
//                    template.header("Authorization", auth); // forward user Bearer token
//                }
//            }
//        };
//    }
//}

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