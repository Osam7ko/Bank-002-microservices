package com.osama.bank002.auth.service;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.Date;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.osama.bank002.auth.domain.entity.User;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class JwtService {
	@Value("${app.jwt.secret}")
	private String secret;
	@Value("${app.jwt.access-minutes}")
	private long accessMinutes;

	private Key key() {
		return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
	}

	public String generateAccessToken(User user) {
		Instant now = Instant.now();
		return Jwts.builder().setSubject(user.getEmail()).claim("uid", user.getId())
				.claim("roles", Arrays.asList(user.getRoles().split(","))).setIssuedAt(Date.from(now))
				.setExpiration(Date.from(now.plus(accessMinutes, ChronoUnit.MINUTES)))
				.signWith(key(), SignatureAlgorithm.HS256).compact();
	}

	public Jws<Claims> parse(String token) {
		return Jwts.parserBuilder().setSigningKey(key()).build().parseClaimsJws(token);
	}
}
