package com.osama.bank002.beneficiary.domain.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record SaveBeneficiaryRequest(
        @NotBlank String accountNumber,
        @Size(max = 120) String beneficiaryName,
        @NotBlank @Size(max = 80) String bankName
) {
}