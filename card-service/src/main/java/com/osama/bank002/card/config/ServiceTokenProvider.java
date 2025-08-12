package com.osama.bank002.card.config;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;
import java.util.List;

@Component
@RequiredArgsConstructor
class ServiceTokenProvider {
    @Value("${app.jwt.secret}")
    private String secret;

    String issueServiceToken() {
        var now = Instant.now();
        return Jwts.builder()
                .setSubject("transaction-service")
                .claim("uid", -1)                 // synthetic uid
                .claim("roles", List.of("SERVICE"))
                .setIssuedAt(Date.from(now))
                .setExpiration(Date.from(now.plusSeconds(300)))
                .signWith(Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8)))
                .compact();
    }
}