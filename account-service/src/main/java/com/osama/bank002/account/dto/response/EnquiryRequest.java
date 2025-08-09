package com.osama.bank002.account.dto.response;

import jakarta.validation.constraints.NotBlank;

public record EnquiryRequest(@NotBlank String accountNumber){
}