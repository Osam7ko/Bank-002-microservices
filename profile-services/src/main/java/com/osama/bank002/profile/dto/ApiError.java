package com.osama.bank002.profile.dto;

import java.time.Instant;

public record ApiError(
        String path,
        int status,
        String error,
        String message,
        Instant timestamp
) {
}