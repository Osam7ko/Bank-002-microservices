package com.osama.bank002.auth.service;

import com.osama.bank002.auth.domain.dto.*;
import com.osama.bank002.auth.domain.entity.RefreshToken;
import com.osama.bank002.auth.domain.entity.User;
import com.osama.bank002.auth.repository.RefreshTokenRepository;
import com.osama.bank002.auth.repository.UserRepository;
import io.jsonwebtoken.Claims;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthAppService {
    private final UserRepository users;
    private final RefreshTokenRepository refreshTokens;
    private final PasswordEncoder encoder;
    private final JwtService jwt;
    @Value("${app.jwt.refresh-days}") private long refreshDays;

    @Transactional
    public void register(RegisterRequest req) {
        if (users.existsByEmail(req.email()))
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Email already used");
        User u = User.builder()
                .email(req.email().toLowerCase())
                .passwordHash(encoder.encode(req.password()))
                .roles("USER")
                .createdAt(LocalDateTime.now())
                .build();
        users.save(u);
    }

    @Transactional
    public AuthResponse login(LoginRequest req) {
        User u = users.findByEmail(req.email().toLowerCase())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid credentials"));
        if (!encoder.matches(req.password(), u.getPasswordHash()))
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid credentials");

        String access = jwt.generateAccessToken(u);
        RefreshToken rt = RefreshToken.builder()
                .id(UUID.randomUUID().toString())
                .userId(u.getId())
                .expiresAt(LocalDateTime.now().plusDays(refreshDays))
                .revoked(false)
                .createdAt(LocalDateTime.now())
                .build();
        refreshTokens.save(rt);
        return new AuthResponse(access, rt.getId());
    }

    @Transactional
    public AuthResponse refresh(RefreshRequest req) {
        RefreshToken rt = refreshTokens.findByIdAndRevokedFalse(req.refreshToken())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid refresh token"));
        if (rt.getExpiresAt().isBefore(LocalDateTime.now()))
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Refresh expired");

        User u = users.findById(rt.getUserId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not found"));

        // rotate token (best practice)
        rt.setRevoked(true);
        String newId = UUID.randomUUID().toString();
        refreshTokens.save(RefreshToken.builder()
                .id(newId).userId(u.getId())
                .expiresAt(LocalDateTime.now().plusDays(refreshDays))
                .revoked(false).createdAt(LocalDateTime.now()).build());

        return new AuthResponse(jwt.generateAccessToken(u), newId);
    }

    public MeResponse me(String bearer) {
        String token = bearer.substring("Bearer ".length());
        Claims c = jwt.parse(token).getBody();
        Long uid = ((Number)c.get("uid")).longValue();
        String email = c.getSubject();
        @SuppressWarnings("unchecked")
        List<String> roles = (List<String>) c.get("roles");
        return new MeResponse(uid, email, roles);
    }
}