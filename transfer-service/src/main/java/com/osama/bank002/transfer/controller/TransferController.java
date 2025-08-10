package com.osama.bank002.transfer.controller;

import com.osama.bank002.transfer.domain.dto.BankResponse;
import com.osama.bank002.transfer.domain.dto.TransferRequest;
import com.osama.bank002.transfer.service.TransferService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/transfers")
@RequiredArgsConstructor
public class TransferController {

    private final TransferService service;

    @PostMapping
    @Operation(summary = "Transfer funds", description = "Debits source then credits destination with idempotency")
    public BankResponse transfer(@AuthenticationPrincipal Jwt jwt,
                                 @Valid @RequestBody TransferRequest req,
                                 @RequestHeader(value = "Idempotency-Key", required = false) String idempotencyKey) {
        return service.transfer(jwt, req, idempotencyKey);
    }
}