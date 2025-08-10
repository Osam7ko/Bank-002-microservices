package com.osama.bank002.transfer.domain.dto;

import java.math.BigDecimal;

public record AccountInfo(
        String accountName,
        String accountNumber,
        BigDecimal accountBalance
) {
}