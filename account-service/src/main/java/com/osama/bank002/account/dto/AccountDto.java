package com.osama.bank002.account.dto;

import java.math.BigDecimal;

public record AccountDto(String accountNumber, String profileId, String displayName,
                         BigDecimal balance, String status) {
}