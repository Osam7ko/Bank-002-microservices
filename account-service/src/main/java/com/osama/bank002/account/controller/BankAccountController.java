package com.osama.bank002.account.controller;


import com.osama.bank002.account.client.ProfileClient;
import com.osama.bank002.account.client.dto.ProfileSummary;
import com.osama.bank002.account.dto.OpenAccountRequest;
import com.osama.bank002.account.dto.response.BankResponse;
import com.osama.bank002.account.dto.response.CreditDebitResponse;
import com.osama.bank002.account.dto.response.EnquiryRequest;
import com.osama.bank002.account.entity.BankAccount;
import com.osama.bank002.account.repository.BankAccountRepository;
import com.osama.bank002.account.service.BankAccountService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;

@RestController
@RequestMapping("/api/accounts")
@RequiredArgsConstructor
public class BankAccountController {

    private final BankAccountService service;
    private final BankAccountRepository repository;
    private final ProfileClient profileClient;

    // Create/open account
    @PostMapping
    @Operation(
            summary = "Create/open account",
            description = "Create an account for the user (profileId required)"
    )
    public BankResponse open(@RequestBody OpenAccountRequest req) {
        return service.openAccount(req.profileId(), req.displayName());
    }

    // Balance enquiry (legacy shape)
    @GetMapping("/{accountNumber}/balance")
    @Operation(
            summary = "Balance enquiry",
            description = "Returns balance wrapped in BankResponse"
    )
    public BankResponse balance(@PathVariable String accountNumber) {
        return service.balanceEnquiry(new EnquiryRequest(accountNumber));
    }

    // Name enquiry (legacy behavior returns String)
    @GetMapping("/{accountNumber}/name")
    @Operation(
            summary = "Name enquiry",
            description = "Returns display name or not exists message"
    )
    public String name(@PathVariable String accountNumber) {
        return service.nameEnquiry(new EnquiryRequest(accountNumber));
    }

    // Credit
    @PostMapping("/{accountNumber}/credit")
    @Operation(
            summary = "Credit", description = "Credits amount to the account"
    )
    public BankResponse credit(@PathVariable String accountNumber,
                               @RequestParam BigDecimal amount) {
        return service.creditAccount(new CreditDebitResponse(accountNumber, amount));
    }

    @GetMapping("/{accountNumber}/owner")
    public ProfileSummary owner(@PathVariable String accountNumber) {
        BankAccount a = repository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        var prof = profileClient.getByProfileId(a.getProfileId());
        return prof; // contains profileId, fullName, email
    }

    // Debit
    @PostMapping("/{accountNumber}/debit")
    @Operation(summary = "Debit", description = "Debits amount from the account")
    public BankResponse debit(@PathVariable String accountNumber,
                              @RequestParam BigDecimal amount) {
        return service.debitAccount(new CreditDebitResponse(accountNumber, amount));
    }

    @GetMapping("/owner/{profileId}/count")
    @Operation(summary = "Count accounts for a profile", description = "Used by profile-service to decide auto-open")
    public int countByOwner(@PathVariable String profileId) {
        return repository.countByProfileId(profileId);
    }
}