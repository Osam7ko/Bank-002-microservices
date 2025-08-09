package com.osama.bank002.account.service;

import com.osama.bank002.account.dto.AccountDto;
import com.osama.bank002.account.dto.BalanceDto;
import com.osama.bank002.account.dto.response.BankResponse;
import com.osama.bank002.account.dto.response.CreditDebitResponse;
import com.osama.bank002.account.dto.response.EnquiryRequest;
import com.osama.bank002.account.entity.BankAccount;

import java.math.BigDecimal;

public interface BankAccountService {

    BankResponse openAccount(String profileId, String displayName);

    BankResponse balanceEnquiry(EnquiryRequest req);

    String nameEnquiry(EnquiryRequest req);

    BankResponse creditAccount(CreditDebitResponse req);

    BankResponse debitAccount(CreditDebitResponse req);

    BankAccount getEntity(String accountNumber);

}