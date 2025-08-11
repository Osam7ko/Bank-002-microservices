package com.osama.bank002.transfer.client;

import com.osama.bank002.transfer.client.transaction.LogTransactionRequest;
import com.osama.bank002.transfer.client.transaction.TransactionDto;
import com.osama.bank002.transfer.config.FeignAuthConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "TRANSACTION-SERVICE", configuration = FeignAuthConfig.class)
public interface TransactionClient {
    @PostMapping("/api/transactions/log")
    TransactionDto log(@RequestBody LogTransactionRequest req);
}