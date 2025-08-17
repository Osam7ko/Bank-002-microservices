package com.osama.bank002.api_gateway.config;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtAuthFilter extends OncePerRequestFilter {
	@Value("${app.jwt.secret}")
	private String secret;

	private Key key() {
		return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
	}

	@Override
	protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res, FilterChain chain)
			throws ServletException, IOException {
		String h = req.getHeader(HttpHeaders.AUTHORIZATION);
		if (h != null && h.startsWith("Bearer ")) {
			String token = h.substring(7);
			try {
				var parser = Jwts.parserBuilder()
						.setSigningKey(key())
						.setAllowedClockSkewSeconds(120) // tolerate skew
						.build();
				Claims c = parser.parseClaimsJws(token).getBody();

				@SuppressWarnings("unchecked")
				List<String> roles = (List<String>) c.get("roles");
				var authorities = roles == null ? List.<GrantedAuthority>of()
						: roles.stream().map(r -> new SimpleGrantedAuthority("ROLE_" + r)).toList();

				var auth = new UsernamePasswordAuthenticationToken(c.getSubject(), null, authorities);
				auth.setDetails(c);
				SecurityContextHolder.getContext().setAuthentication(auth);

				System.out.println("GATEWAY JWT OK: sub=" + c.getSubject() + ", roles=" + roles);
			} catch (io.jsonwebtoken.ExpiredJwtException ex) {
				System.out.println("GATEWAY JWT FAIL: ExpiredJwtException - " + ex.getMessage());
			} catch (io.jsonwebtoken.security.SignatureException ex) {
				System.out.println("GATEWAY JWT FAIL: SignatureException - " + ex.getMessage());
			} catch (io.jsonwebtoken.JwtException ex) {
				System.out.println("GATEWAY JWT FAIL: " + ex.getClass().getSimpleName() + " - " + ex.getMessage());
			}
		}
		chain.doFilter(req, res);
	}
}