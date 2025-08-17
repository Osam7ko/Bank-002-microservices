package com.osama.bank002.transfer.controller;

import com.osama.bank002.transfer.domain.dto.BankResponse;
import com.osama.bank002.transfer.domain.dto.TransferRequest;
import com.osama.bank002.transfer.service.TransferService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/transfers")
@RequiredArgsConstructor
public class TransferController {

    private final TransferService service;

    @PostMapping
    @Operation(summary = "Transfer funds", description = "Debits source then credits destination with idempotency")
    public BankResponse transfer(@Valid @RequestBody TransferRequest req,
                                 @RequestHeader(value = "Idempotency-Key", required = false) String idempotencyKey) {
        System.out.println("TRANSFER CTRL: from=" + req.fromAccount() + ", to=" + req.toAccount() + ", amt=" + req.amount());
        return service.transfer(req, idempotencyKey);
    }
}