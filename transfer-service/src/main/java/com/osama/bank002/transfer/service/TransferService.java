package com.osama.bank002.transfer.service;

import com.osama.bank002.transfer.domain.dto.BankResponse;
import com.osama.bank002.transfer.domain.dto.TransferRequest;
import jakarta.annotation.Nullable;
import org.springframework.security.oauth2.jwt.Jwt;

public interface TransferService {

    BankResponse transfer(Jwt jwt, TransferRequest req, @Nullable String idemHeader);
}