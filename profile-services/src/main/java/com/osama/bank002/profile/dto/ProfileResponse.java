package com.osama.bank002.profile.dto;

import java.time.LocalDateTime;

public record ProfileResponse(
        Long id,
        String userId,
        String firstName,
        String lastName,
        String otherName,
        String email,
        String gender,
        String address,
        String stateOfOrigin,
        String phoneNumber,
        String alternativePhoneNumber,
        String status,
        LocalDateTime createdAt,
        LocalDateTime modifiedAt
) {
}