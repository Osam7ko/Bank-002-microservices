package com.osama.bank002.account.dto.transaction;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record LogTransactionRequest(@NotBlank String accountNumber,
                                    @NotBlank String transactionType,
                                    @NotNull BigDecimal amount,
                                    @NotBlank String status,
                                    LocalDateTime createdAt) {
}