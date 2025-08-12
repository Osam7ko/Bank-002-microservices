package com.osama.bank002.profile.service;

import com.osama.bank002.profile.dto.ProfileUpdateRequest;
import com.osama.bank002.profile.entity.Profile;

public interface ProfileService {

    Profile bootstrapIfMissing();

    Profile getMyProfile();

    Profile updateMyProfile(ProfileUpdateRequest request);

    Profile getByUserId(String userId);

    void softDeleteMyProfile();

    Profile getByProfileId(Long profileId);
}