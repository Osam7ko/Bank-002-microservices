package com.osama.bank002.card.client.dto;

import java.math.BigDecimal;

public record AccountDto(
        String accountNumber, String profileId, String displayName,
        BigDecimal balance, String status
) {}