package com.osama.bank002.account.dto.response;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record CreditDebitResponse(@NotBlank String accountNumber , @NotNull @Positive BigDecimal amount) {
}