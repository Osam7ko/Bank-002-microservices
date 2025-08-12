package com.osama.bank002.card.controller;

import com.osama.bank002.card.domain.dto.*;
import com.osama.bank002.card.service.CardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cards")
@RequiredArgsConstructor
@Tag(name = "Card Management")
public class CardController {

    private final CardService service;

    @PostMapping
    @Operation(summary = "Issue card")
    public CardDto issue(@Valid @RequestBody IssueCardRequest req) {
        return service.issue(req);
    }

    @GetMapping
    @Operation(summary = "List cards for account")
    public List<CardDto> list(@RequestParam String accountNumber) {
        return service.listByAccount(accountNumber);
    }

    @PatchMapping("/{cardId}/status")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Change card status")
    public void changeStatus(@PathVariable Long cardId, @Valid @RequestBody UpdateCardStatusRequest req) {
        service.changeStatus(cardId, req.status());
    }

    @PostMapping("/verify")
    @Operation(summary = "Verify card (for payment-service)")
    public VerifyCardResponse verify(@Valid @RequestBody VerifyCardRequest req) {
        return service.verify(req);
    }
}