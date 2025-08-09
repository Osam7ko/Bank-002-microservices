package com.osama.bank002.profile.client;

import com.osama.bank002.profile.client.dto.BankResponse;
import com.osama.bank002.profile.client.dto.OpenAccountRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;

@FeignClient(name = "ACCOUNT-SERVICE")
public interface AccountClient {

    @PostMapping("/api/accounts")
    BankResponse opne(OpenAccountRequest req);

}