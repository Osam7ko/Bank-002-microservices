package com.osama.bank002.transaction.domain.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "ledger_entries", indexes = {
        @Index(name = "idx_ledger_acct_date", columnList = "accountNumber,createdAt")
})
public class LedgerEntry {

    @Id
    private String id;

    @Column(nullable = false)
    private String accountNumber;

    @Column(nullable = false)
    private String transactionType; // CREDIT, DEBIT, TRANSFER_IN, TRANSFER_OUT

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal amount;

    @Column(nullable = false)
    private String status; // SUCCESS/FAILED

    @Column(nullable = false)
    private LocalDateTime createdAt; // event time
}