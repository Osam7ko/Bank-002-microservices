package com.osama.bank002.beneficiary.domain.dto;

public record SavedBeneficiaryDto(
        Long id, String beneficiaryName, String accountNumber, String bankName
) {}