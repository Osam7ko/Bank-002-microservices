package com.osama.bank002.beneficiary.controller;

import com.osama.bank002.beneficiary.domain.dto.SaveBeneficiaryRequest;
import com.osama.bank002.beneficiary.domain.dto.SavedBeneficiaryDto;
import com.osama.bank002.beneficiary.service.BeneficiaryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/beneficiaries")
@RequiredArgsConstructor
@Tag(name = "Beneficiaries")
public class BeneficiaryController {

    private final BeneficiaryService service;

    @PostMapping
    @Operation(summary = "Save a beneficiary for current user")
    public SavedBeneficiaryDto save(@Valid @RequestBody SaveBeneficiaryRequest req) {
        return service.save(req);
    }

    @GetMapping
    @Operation(summary = "List my beneficiaries")
    public List<SavedBeneficiaryDto> list() {
        return service.listMine();
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Delete a beneficiary by id (must be owner)")
    public void deleteById(@PathVariable Long id) {
        service.deleteById(id);
    }

    @DeleteMapping("/by-account/{accountNumber}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Delete a beneficiary by account number (must be owner)")
    public void deleteByAccount(@PathVariable String accountNumber) {
        service.deleteByAccountNumber(accountNumber);
    }
}