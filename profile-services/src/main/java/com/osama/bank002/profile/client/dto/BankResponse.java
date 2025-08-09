package com.osama.bank002.profile.client.dto;

public record BankResponse(
        String responseCode,
        String responseMessage,
        AccountInfo accountInfo
) {
    public record AccountInfo(String accountName, String accountNumber, java.math.BigDecimal accountBalance) {
    }
}