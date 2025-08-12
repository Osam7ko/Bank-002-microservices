package com.osama.bank002.profile.client;

import com.osama.bank002.profile.client.dto.BankResponse;
import com.osama.bank002.profile.client.dto.OpenAccountRequest;
import com.osama.bank002.profile.config.FeignAuthConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "ACCOUNT-SERVICE", configuration = FeignAuthConfig.class)
public interface AccountClient {
    @PostMapping("/api/accounts")
    BankResponse open(@RequestBody OpenAccountRequest req);

    @GetMapping("/api/accounts/owner/{profileId}/count")
    int countOpenAccounts(@PathVariable("profileId") String profileId);
}