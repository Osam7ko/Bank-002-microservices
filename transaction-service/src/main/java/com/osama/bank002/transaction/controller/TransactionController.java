package com.osama.bank002.transaction.controller;

import com.osama.bank002.transaction.domain.dto.LogTransactionRequest;
import com.osama.bank002.transaction.domain.dto.TransactionDto;
import com.osama.bank002.transaction.service.TransactionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Tag(name = "Transactions & Statements")
public class TransactionController {

    private final TransactionService service;

    // Called by account-service / transfer-service to record an event
    @PreAuthorize("hasAuthority('SERVICE') or hasAuthority('ADMIN')")
    @PostMapping("/transactions/log")
    @Operation(summary = "Log a transaction")
    public TransactionDto log(@Valid @RequestBody LogTransactionRequest req) {
        return service.log(req);
    }

    @GetMapping("/transactions")
    @Operation(summary = "List transactions in date range")
    public List<TransactionDto> list(@RequestParam String accountNumber,
                                     @RequestParam String from,
                                     @RequestParam String to) {
        return service.list(accountNumber, from, to);
    }

    @GetMapping(value = "/statements/pdf", produces = "application/pdf")
    @Operation(summary = "Generate statement PDF (optionally email it)")
    public ResponseEntity<byte[]> pdf(@RequestParam String accountNumber,
                                      @RequestParam String from,
                                      @RequestParam String to,
                                      @RequestParam(defaultValue = "false") boolean email) {
        byte[] pdf = service.statementPdf(accountNumber, from, to, email);
        String filename = "statement-" + accountNumber + "-" + from + "_to_" + to + ".pdf";
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=" + filename)
                .body(pdf);
    }
}