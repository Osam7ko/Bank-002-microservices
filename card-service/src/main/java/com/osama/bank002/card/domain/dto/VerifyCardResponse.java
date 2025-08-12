package com.osama.bank002.card.domain.dto;

public record VerifyCardResponse(
        boolean approved,
        String reason,            // "OK" | "CARD_BLOCKED" | "CVV_MISMATCH" | "EXPIRED" | ...
        String signature          // echo valid signature if approved (or null)
) {}