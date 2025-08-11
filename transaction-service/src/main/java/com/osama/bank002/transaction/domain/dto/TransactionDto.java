package com.osama.bank002.transaction.domain.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record TransactionDto(String id, String accountNumber,
                             String transactionType, BigDecimal amount,
                             String status, LocalDateTime createdAt) {
}