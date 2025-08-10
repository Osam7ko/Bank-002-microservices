package com.osama.bank002.beneficiary.domain.dto;

import jakarta.validation.constraints.NotBlank;

public record SaveBeneficiaryRequest(
        @NotBlank String accountNumber,
        @NotBlank String beneficiaryName,
        String bankName
) {}