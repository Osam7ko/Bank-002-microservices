package com.osama.bank002.account.mapper;

import com.osama.bank002.account.dto.AccountDto;
import com.osama.bank002.account.entity.BankAccount;

public final class AccountMapper {

    // Private constructor to prevent instantiation of this utility class.
    private AccountMapper() {
        // This is a utility class
    }
    public static AccountDto toDto(BankAccount account) {
        return new AccountDto(
                account.getAccountNumber(),
                account.getProfileId(),
                account.getDisplayName(),
                account.getBalance(),
                account.getStatus()
        );
    }

    public static void apply(AccountDto dto, BankAccount account) {
        account.setAccountNumber(dto.accountNumber());
        account.setProfileId(dto.profileId());
        account.setDisplayName(dto.displayName());
        account.setBalance(dto.balance());
        account.setStatus(dto.status());
    }
}