package com.osama.bank002.card.domain.dto;

import jakarta.validation.constraints.NotBlank;

public record IssueCardRequest(
        @NotBlank String accountNumber,
        @NotBlank String cardType,        // VISA, MASTER, MADA
        String nameOnCard,
        @NotBlank String expiryMonth,     // "02"
        @NotBlank String expiryYear,      // "2030"
        @NotBlank String cvv              // plain from UI, hash inside service
) {}