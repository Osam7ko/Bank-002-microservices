package com.osama.bank002.transfer.domain.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record TransferRequest(@NotBlank String fromAccount,
                              @NotBlank String toAccount,
                              @NotNull @DecimalMin("0.01") BigDecimal amount,
                              String idempotencyKey) {
}