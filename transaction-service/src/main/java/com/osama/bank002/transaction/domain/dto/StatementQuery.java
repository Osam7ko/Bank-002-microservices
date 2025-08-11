package com.osama.bank002.transaction.domain.dto;

import jakarta.validation.constraints.NotBlank;

public record StatementQuery(@NotBlank String accountNumber,
                             @NotBlank String from,
                             @NotBlank String to) {
}
// to,from ISO yyyy-MM-dd