package com.osama.bank002.transfer.service;

import com.osama.bank002.transfer.domain.dto.BankResponse;
import com.osama.bank002.transfer.domain.dto.TransferRequest;
import jakarta.annotation.Nullable;

public interface TransferService {

    BankResponse transfer(TransferRequest req, @Nullable String idemHeader);
}