package com.osama.bank002.profile.controller;

import com.osama.bank002.profile.dto.ProfileResponse;
import com.osama.bank002.profile.dto.ProfileUpdateRequest;
import com.osama.bank002.profile.entity.Profile;
import com.osama.bank002.profile.mapper.ProfileMapper;
import com.osama.bank002.profile.service.ProfileService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/profiles")
@Tag(name = "Profile Management API's")
public class ProfileController {

    private ProfileService service;

    @PostMapping("/bootstrap")
    @Operation(
            summary = "Checking exists",
            description = "First call after login to ensure a row exists"
    )
    @ApiResponse(
            responseCode = "200",
            description = "Http Status 200 OK"
    )
    public ProfileResponse bootstrap(@AuthenticationPrincipal Jwt jwt) {
        Profile p = service.bootstrapIfMissing(jwt);
        return ProfileMapper.toDto(p);
    }

    @GetMapping("/me")
    @Operation(
            summary = "Get User",
            description = "Given the jwt and find the user"
    )
    @ApiResponse(
            responseCode = "200",
            description = "Http Status 200 OK"
    )
    public ProfileResponse me(@AuthenticationPrincipal Jwt jwt) {
        return ProfileMapper.toDto(service.getMyProfile(jwt));
    }

    @PutMapping("/me")
    @Operation(
            summary = "Update user",
            description = "Given the jwt and update the user"
    )
    @ApiResponse(
            responseCode = "201",
            description = "Http Status 200 SUCCESS"
    )
    public ProfileResponse update(@AuthenticationPrincipal Jwt jwt, @Valid @RequestBody ProfileUpdateRequest req) {
        return ProfileMapper.toDto(service.updateMyProfile(jwt, req));
    }

    // Optional admin endpoint to fetch by userId
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/{userId}")
    public ProfileResponse getByUserId(@PathVariable String userId) {
        return ProfileMapper.toDto(service.getByUserId(userId));
    }

    @DeleteMapping("/me")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteMyProfile(@AuthenticationPrincipal Jwt jwt) {
        service.softDeleteMyProfile(jwt); // throws 409 if accounts exist
    }
}