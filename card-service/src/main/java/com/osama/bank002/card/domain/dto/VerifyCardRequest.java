package com.osama.bank002.card.domain.dto;

import jakarta.validation.constraints.NotBlank;

public record VerifyCardRequest(
        @NotBlank String cardNumber,
        @NotBlank String expiryMonth,
        @NotBlank String expiryYear,
        @NotBlank String cvv
) {}