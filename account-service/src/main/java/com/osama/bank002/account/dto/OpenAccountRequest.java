package com.osama.bank002.account.dto;

public record OpenAccountRequest(
        String profileId,       // or null → derive from token via profile-service if you prefer
        String displayName      // optional snapshot (or fetch from profile-service)
) {
}