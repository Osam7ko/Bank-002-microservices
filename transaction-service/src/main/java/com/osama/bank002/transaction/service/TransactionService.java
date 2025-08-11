package com.osama.bank002.transaction.service;

import com.osama.bank002.transaction.domain.dto.LogTransactionRequest;
import com.osama.bank002.transaction.domain.dto.TransactionDto;

import java.util.List;

public interface TransactionService {

    TransactionDto log(LogTransactionRequest req);

    List<TransactionDto> list(String accountNumber, String from, String to);

    byte[] statementPdf(String accountNumber, String from, String to, boolean email);


}