package com.osama.bank002.transaction.mapper;

import com.osama.bank002.transaction.domain.dto.TransactionDto;
import com.osama.bank002.transaction.domain.entity.LedgerEntry;

public final class TxMapper {
    private TxMapper() {
    }

    public static TransactionDto toDto(LedgerEntry e) {
        return new TransactionDto(e.getId(), e.getAccountNumber(), e.getTransactionType(),
                e.getAmount(), e.getStatus(), e.getCreatedAt());
    }
}