package com.osama.bank002.transfer.client;

import com.osama.bank002.transfer.config.FeignAuthConfig;
import com.osama.bank002.transfer.domain.dto.BankResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;

@FeignClient(name = "ACCOUNT-SERVICE", configuration = FeignAuthConfig.class)
public interface AccountClient {

    @PostMapping("/api/accounts/{accountNumber}/debit")
    BankResponse debit(@PathVariable String accountNumber,
                       @RequestParam BigDecimal amount);

    @PostMapping("/api/accounts/{accountNumber}/credit")
    BankResponse credit(@PathVariable String accountNumber,
                        @RequestParam BigDecimal amount);

    @GetMapping("/api/accounts/{accountNumber}/balance")
    BankResponse balance(@PathVariable String accountNumber);

    @GetMapping("/api/accounts/{accountNumber}/name")
    String name(@PathVariable String accountNumber);
}