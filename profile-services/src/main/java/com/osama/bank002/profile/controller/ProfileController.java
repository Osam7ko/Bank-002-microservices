package com.osama.bank002.profile.controller;

import com.osama.bank002.profile.dto.ProfileResponse;
import com.osama.bank002.profile.dto.ProfileUpdateRequest;
import com.osama.bank002.profile.mapper.ProfileMapper;
import com.osama.bank002.profile.service.ProfileService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/profiles")
@Tag(name = "Profile Management API's")
@RequiredArgsConstructor
public class ProfileController {

    private final ProfileService service;

    // ProfileController
    @GetMapping("/id/{profileId}")
    public ProfileResponse getByProfileId(@PathVariable Long profileId) {
        return ProfileMapper.toDto(service.getByProfileId(profileId));
    }

    @PostMapping("/bootstrap")
    @Operation(
            summary = "Checking exists",
            description = "Ensure profile exists & open default account once"
    )
    @ApiResponse(
            responseCode = "200",
            description = "Http Status 200 OK"
    )
    public ProfileResponse bootstrap() {
        return ProfileMapper.toDto(service.bootstrapIfMissing());
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
    public ProfileResponse me() {
        return ProfileMapper.toDto(service.getMyProfile());
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
    public ProfileResponse update(@Valid @RequestBody ProfileUpdateRequest req) {
        return ProfileMapper.toDto(service.updateMyProfile(req));
    }

    // Optional admin endpoint to fetch by userId
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/{userId}")
    public ProfileResponse getByUserId(@PathVariable String userId) {
        return ProfileMapper.toDto(service.getByUserId(userId));
    }

    @DeleteMapping("/me")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteMyProfile() {
        service.softDeleteMyProfile();
    }
}