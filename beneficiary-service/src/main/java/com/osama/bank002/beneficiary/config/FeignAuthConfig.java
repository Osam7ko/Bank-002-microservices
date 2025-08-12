package com.osama.bank002.beneficiary.config;


import com.osama.bank002.beneficiary.domain.dto.UserPrincipal;
import feign.RequestInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;


@Configuration
@RequiredArgsConstructor
public class FeignAuthConfig {

    private final ServiceTokenProvider serviceTokenProvider;

    @Bean
    public RequestInterceptor relayAuthToken() {
        return template -> {
            // 1) Try to get the caller's bearer token placed by JwtAuthFilter
            String bearer = resolveBearerFromRequestContext();
            if (bearer == null) bearer = resolveBearerFromSecurityContext();

            if (bearer != null) {
                template.header("Authorization", bearer);
                return;
            }

            // 2) No caller token -> mint a short-lived service token
            String serviceBearer = "Bearer " + serviceTokenProvider.issueServiceToken();
            template.header("Authorization", serviceBearer);
        };
    }

    private String resolveBearerFromRequestContext() {
        var attrs = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attrs == null) return null;
        Object raw = attrs.getRequest().getAttribute("AUTH_BEARER");
        return (raw instanceof String s && !s.isBlank()) ? "Bearer " + s : null;
    }

    private String resolveBearerFromSecurityContext() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null) return null;

        // If you stored the token string in details in your JwtAuthFilter:
        Object details = auth.getDetails();
        if (details instanceof String s && s.startsWith("Bearer ")) return s;

        // Or if your principal carries it:
        if (auth.getPrincipal() instanceof UserPrincipal p && p.token() != null) {
            return "Bearer " + p.token();
        }
        return null;
    }
}