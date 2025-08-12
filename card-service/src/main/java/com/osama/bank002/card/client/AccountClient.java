package com.osama.bank002.card.client;

import com.osama.bank002.card.client.dto.AccountDto;
import com.osama.bank002.card.config.FeignAuthConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "ACCOUNT-SERVICE", configuration = FeignAuthConfig.class)
public interface AccountClient {
    @GetMapping("/api/accounts/{accountNumber}")
    AccountDto get(@PathVariable String accountNumber);
}