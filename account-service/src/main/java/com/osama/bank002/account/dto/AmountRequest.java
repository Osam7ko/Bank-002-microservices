package com.osama.bank002.account.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record AmountRequest(@NotNull @Positive BigDecimal amount) {
}