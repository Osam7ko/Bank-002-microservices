package com.osama.bank002.profile.util;

import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;

@Component
public class JwtUtils {
    public String userId(Jwt jwt) {
        return jwt.getSubject();
    }

    public String email(Jwt jwt) {
        Object claim = jwt.getClaims().get("email");
        return claim != null ? claim.toString() : null;
    }
}