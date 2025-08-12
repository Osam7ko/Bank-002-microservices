package com.osama.bank002.card.domain.dto;

import java.time.LocalDateTime;

public record CardDto(
        Long id, String maskedPan, String accountNumber,
        String cardType, String status, String expiryMonth, String expiryYear,
        String nameOnCard, LocalDateTime createdAt
) {}