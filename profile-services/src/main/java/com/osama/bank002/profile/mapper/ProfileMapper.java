package com.osama.bank002.profile.mapper;

import com.osama.bank002.profile.dto.ProfileResponse;
import com.osama.bank002.profile.dto.ProfileUpdateRequest;
import com.osama.bank002.profile.entity.Profile;

public class ProfileMapper {

    public static ProfileResponse toDto(Profile p) {
        return new ProfileResponse(
                p.getId(), p.getUserId(), p.getFirstName(), p.getLastName(), p.getOtherName(),
                p.getEmail(),
                p.getGender(), p.getAddress(), p.getStateOfOrigin(),
                p.getPhoneNumber(), p.getAlternativePhoneNumber(),
                p.getStatus(), p.getCreatedAt(), p.getModifiedAt()
        );
    }

    public static void apply(ProfileUpdateRequest r, Profile p) {
        p.setFirstName(r.firstName());
        p.setLastName(r.lastName());
        p.setOtherName(r.otherName());
        p.setEmail(r.email());
        p.setGender(r.gender());
        p.setAddress(r.address());
        p.setStateOfOrigin(r.stateOfOrigin());
        p.setPhoneNumber(r.phoneNumber());
        p.setAlternativePhoneNumber(r.alternativePhoneNumber());
    }
}