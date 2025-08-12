package com.osama.bank002.beneficiary.util;

import io.jsonwebtoken.Claims;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class JwtUtils {
    private Claims claims() {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        return auth != null && auth.getDetails() instanceof Claims c ? c : null;
    }

    /**
     * Auth-service puts numeric uid in "uid" claim
     */
    public Long userId() {
        var c = claims();
        return c == null ? null : ((Number) c.get("uid")).longValue();
    }

    /**
     * Convenient if your DB column is String
     */
    public String userIdString() {
        var id = userId();
        return id == null ? null : String.valueOf(id);
    }

    public String email() {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        return auth == null ? null : (String) auth.getPrincipal(); // subject = email
    }

    @SuppressWarnings("unchecked")
    public List<String> roles() {
        var c = claims();
        return c == null ? List.of() : (List<String>) c.get("roles");
    }
}