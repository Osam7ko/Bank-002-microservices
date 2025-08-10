package com.osama.bank002.beneficiary.client;

import com.osama.bank002.beneficiary.config.FeignAuthConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "ACCOUNT-SERVICE", configuration = FeignAuthConfig.class)
public interface AccountClient {
    @GetMapping("/api/accounts/{accountNumber}/name")
    String name(@PathVariable String accountNumber);
}