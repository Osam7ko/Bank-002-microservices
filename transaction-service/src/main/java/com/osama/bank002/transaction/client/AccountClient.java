package com.osama.bank002.transaction.client;

import com.osama.bank002.transaction.config.FeignAuthConfig;
import com.osama.bank002.transaction.domain.dto.ProfileSummary;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "ACCOUNT-SERVICE", configuration = FeignAuthConfig.class)
public interface AccountClient {
    @GetMapping("/api/accounts/{accountNumber}/owner")
    ProfileSummary owner(@PathVariable String accountNumber);
}