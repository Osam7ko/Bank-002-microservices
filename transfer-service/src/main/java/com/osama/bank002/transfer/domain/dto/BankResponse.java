package com.osama.bank002.transfer.domain.dto;

import lombok.Builder;

@Builder
public record BankResponse(
        String responseCode,
        String responseMessage,
        AccountInfo accountInfo
) {
}