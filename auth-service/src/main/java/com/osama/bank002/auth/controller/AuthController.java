package com.osama.bank002.auth.controller;

import com.osama.bank002.auth.domain.dto.*;
import com.osama.bank002.auth.service.AuthAppService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthAppService service;

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public void register(@RequestBody RegisterRequest req) {
        service.register(req);
    }

    @PostMapping("/login")
    public AuthResponse login(@RequestBody LoginRequest req) {
        return service.login(req);
    }

    @PostMapping("/refresh")
    public AuthResponse refresh(@RequestBody RefreshRequest req) {
        return service.refresh(req);
    }

    @GetMapping("/me")
    public MeResponse me(@RequestHeader("Authorization") String bearer) {
        return service.me(bearer);
    }
}