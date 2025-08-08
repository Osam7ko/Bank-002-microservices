package com.osama.bank002.profile.service;

import com.osama.bank002.profile.dto.ProfileUpdateRequest;
import com.osama.bank002.profile.entity.Profile;
import org.springframework.security.oauth2.jwt.Jwt;

public interface ProfileService {

    Profile bootstrapIfMissing(Jwt jwt);

    Profile getMyProfile(Jwt jwt);

    Profile updateMyProfile(Jwt jwt, ProfileUpdateRequest request);

    Profile getByUserId(String userId);

    void softDeleteMyProfile(Jwt jwt);
}