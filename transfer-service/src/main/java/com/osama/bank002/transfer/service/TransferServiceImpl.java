package com.osama.bank002.transfer.service;

import com.osama.bank002.transfer.client.AccountClient;
import com.osama.bank002.transfer.client.TransactionClient;
import com.osama.bank002.transfer.client.transaction.LogTransactionRequest;
import com.osama.bank002.transfer.domain.dto.BankResponse;
import com.osama.bank002.transfer.domain.dto.TransferRequest;
import com.osama.bank002.transfer.domain.entity.Transfer;
import com.osama.bank002.transfer.repository.TransferRepository;
import com.osama.bank002.transfer.util.AccountUtils;
import com.osama.bank002.transfer.util.SecurityUtils;
import jakarta.annotation.Nullable;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TransferServiceImpl implements TransferService {

    private final TransferRepository repo;
    private final AccountClient accountClient;
    private final TransactionClient transactionClient;

    @Transactional
    @Override
    public BankResponse transfer(TransferRequest req, @Nullable String idemHeader) {
        String requester = SecurityUtils.currentUserId();
        String idem = (req.idempotencyKey() != null && !req.idempotencyKey().isBlank())
                ? req.idempotencyKey()
                : (idemHeader != null && !idemHeader.isBlank() ? idemHeader : UUID.randomUUID().toString());

        var existing = repo.findByRequesterUserIdAndIdempotencyKey(requester, idem);
        if (existing.isPresent()) {
            var t = existing.get();
            if ("SUCCESS".equals(t.getStatus())) {
                return BankResponse.builder()
                        .responseCode(AccountUtils.TRANSFER_SUCCESS_CODE)
                        .responseMessage(AccountUtils.TRANSFER_SUCCESS_MESSAGE)
                        .accountInfo(null).build();
            }
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Duplicate transfer still pending/failed");
        }

        Transfer pendingTransfer = Transfer.builder()
                .id(UUID.randomUUID().toString())
                .requesterUserId(requester)
                .fromAccount(req.fromAccount())
                .toAccount(req.toAccount())
                .amount(req.amount())
                .status("PENDING")
                .idempotencyKey(idem)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        repo.save(pendingTransfer);

//        Check Balance
        var balanceResp = accountClient.balance(req.fromAccount());
        var currentBalance = balanceResp.accountInfo() != null ? balanceResp.accountInfo().accountBalance() : null;

        if (currentBalance == null || currentBalance.compareTo(req.amount()) < 0) {
            pendingTransfer.setStatus("FAILED");
            pendingTransfer.setUpdatedAt(LocalDateTime.now());
            repo.save(pendingTransfer);
            return BankResponse.builder()
                    .responseCode(AccountUtils.INSUFFICIENT_BALANCE_CODE)
                    .responseMessage(AccountUtils.INSUFFICIENT_BALANCE_MESSAGE)
                    .accountInfo(null).build();
        }

//        Debit
        var debitResp = accountClient.debit(req.fromAccount(), req.amount());
        if (!AccountUtils.ACCOUNT_DEBITED_SUCCESS_CODE.equals(debitResp.responseCode())) {
            pendingTransfer.setStatus("FAILED");
            pendingTransfer.setUpdatedAt(LocalDateTime.now());
            repo.save(pendingTransfer);
            return debitResp;
        }

//        Credit
        try {
            var creditResp = accountClient.credit(req.toAccount(), req.amount());
            if (!AccountUtils.ACCOUNT_CREDITED_SUCCESS_CODE.equals(creditResp.responseCode())) {
                // compensate
                accountClient.credit(req.fromAccount(), req.amount());
                pendingTransfer.setStatus("FAILED");
                pendingTransfer.setUpdatedAt(LocalDateTime.now());
                repo.save(pendingTransfer);
                return creditResp;
            }
        } catch (Exception ex) {
            // compensate best-effort
            try {
                accountClient.credit(req.fromAccount(), req.amount());
            } catch (Exception ignored) {
            }
            pendingTransfer.setStatus("FAILED");
            pendingTransfer.setUpdatedAt(LocalDateTime.now());
            repo.save(pendingTransfer);
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "Credit failed; compensated", ex);
        }
        // Transaction Alert
        transactionClient.log(new LogTransactionRequest(
                pendingTransfer.getRequesterUserId(), "TRANSFER", req.amount(), "SUCCESS", LocalDateTime.now()
        ));
        // success
        pendingTransfer.setStatus("SUCCESS");
        pendingTransfer.setUpdatedAt(LocalDateTime.now());
        repo.save(pendingTransfer);

        return BankResponse.builder()
                .responseCode(AccountUtils.TRANSFER_SUCCESS_CODE)
                .responseMessage(AccountUtils.TRANSFER_SUCCESS_MESSAGE)
                .accountInfo(null)
                .build();
    }
}