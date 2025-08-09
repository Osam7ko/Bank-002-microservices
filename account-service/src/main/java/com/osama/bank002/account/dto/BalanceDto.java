package com.osama.bank002.account.dto;

import java.math.BigDecimal;

public record BalanceDto(String accountNumber, BigDecimal balance) {
}