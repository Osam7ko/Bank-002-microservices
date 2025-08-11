package com.osama.bank002.account.client;

import com.osama.bank002.account.config.FeignAuthConfig;
import com.osama.bank002.account.dto.transaction.LogTransactionRequest;
import com.osama.bank002.account.dto.transaction.TransactionDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "TRANSACTION-SERVICE", configuration = FeignAuthConfig.class)
public interface TransactionClient {
    @PostMapping("/api/transactions/log")
    TransactionDto log(@RequestBody LogTransactionRequest req);
}