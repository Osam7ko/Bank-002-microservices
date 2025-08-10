package com.osama.bank002.transfer.util;

import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;

@Component
public class JwtUtils {
    public String userId(Jwt jwt) { return jwt.getSubject(); }
}