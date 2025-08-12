package com.osama.bank002.account.service;

import com.osama.bank002.account.client.ProfileClient;
import com.osama.bank002.account.client.TransactionClient;
import com.osama.bank002.account.client.dto.ProfileSummary;
import com.osama.bank002.account.dto.AccountDto;
import com.osama.bank002.account.dto.EmailDetails;
import com.osama.bank002.account.dto.response.AccountInfo;
import com.osama.bank002.account.dto.response.BankResponse;
import com.osama.bank002.account.dto.response.CreditDebitResponse;
import com.osama.bank002.account.dto.response.EnquiryRequest;
import com.osama.bank002.account.dto.transaction.LogTransactionRequest;
import com.osama.bank002.account.entity.BankAccount;
import com.osama.bank002.account.repository.BankAccountRepository;
import com.osama.bank002.account.utils.AccountUtils;
import com.osama.bank002.account.utils.Money;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.http.HttpStatus;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.springframework.http.ResponseEntity.notFound;

@Service
@RequiredArgsConstructor
@Slf4j
public class BankAccountServiceImpl implements BankAccountService {

    private final BankAccountRepository repo;
    private final EmailService emailService;
    private final ProfileClient profileClient;
    private final TransactionClient transactionClient;

    private static final int MAX_RETRIES = 3;

    @Transactional
    @Override
    public BankResponse openAccount(String profileId, String displayName) {
        var profile = profileClient.getByProfileId(profileId);

        String accountNumber = AccountUtils.generateAccountNumber();
        String finalDisplay = (displayName == null || displayName.isBlank())
                ? profile.fullName()
                : displayName;

        BankAccount savedAccount = repo.save(BankAccount.builder()
                .accountNumber(accountNumber)
                .profileId(profileId)
                .displayName(finalDisplay)
                .balance(BigDecimal.ZERO)
                .status("ACTIVE")
                .build());

        // Best-effort email; never fail the transaction for a mail error
        try {
            if (org.springframework.util.StringUtils.hasText(profile.email())) {
                emailService.sendEmailAlert(EmailDetails.builder()
                        .recipient(profile.email())
                        .subject("Account creation")
                        .messageBody("""
                Congrats! Your account has been created.
                Account Name: %s
                Account Number: %s
                """.formatted(finalDisplay, accountNumber))
                        .build());
            } else {
                log.warn("Profile {} has no email; skipping creation email", profileId);
            }
        } catch (Exception e) {
            log.error("Non-fatal: email failed for profile {}", profileId, e);
        }

        return BankResponse.builder()
                .responseCode(AccountUtils.ACCOUNT_CREATION_SUCCESS)
                .responseMessage(AccountUtils.ACCOUNT_CREATION_MESSAGE)
                .accountInfo(AccountInfo.builder()
                        .accountName(finalDisplay)
                        .accountNumber(accountNumber)
                        .accountBalance(savedAccount.getBalance())
                        .build())
                .build();
    }

    @Transactional(readOnly = true)
    @Override
    public BankResponse balanceEnquiry(EnquiryRequest req) {
        return repo.findByAccountNumber(req.accountNumber())
                .map(a -> BankResponse.builder()
                        .responseCode(AccountUtils.ACCOUNT_FOUND_CODE)
                        .responseMessage(AccountUtils.ACCOUNT_FOUND_SUCCESS)
                        .accountInfo(AccountInfo.builder()
                                .accountName(a.getDisplayName())
                                .accountNumber(a.getAccountNumber())
                                .accountBalance(a.getBalance())
                                .build())
                        .build())
                .orElseGet(this::notFound);
    }

    @Transactional(readOnly = true)
    @Override
    public String nameEnquiry(EnquiryRequest req) {
        return repo.findByAccountNumber(req.accountNumber())
                .map(a -> (a.getDisplayName() == null || a.getDisplayName().isBlank())
                        ? ("Account " + a.getAccountNumber())
                        : a.getDisplayName())
                .orElse(AccountUtils.ACCOUNT_NOT_EXISTS_MESSAGE);
    }

    @Transactional
    @Override
    public BankResponse creditAccount(CreditDebitResponse req) {
        BigDecimal amt = Money.normalize(req.amount());

        for (int i = 0; i < MAX_RETRIES; i++) {
            try {
                var acc = repo.findByAccountNumber(req.accountNumber()).orElse(null);
                if (acc == null) return notFound();
                if (!"ACTIVE".equalsIgnoreCase(acc.getStatus())) return blocked("Account is not ACTIVE");

                acc.setBalance(acc.getBalance().add(amt));
                // JPA flush on commit; @Version will throw if concurrent update

                tryNotifyAndLogCredit(acc, amt);
                return okCredit(acc);

            } catch (org.springframework.dao.OptimisticLockingFailureException e) {
                if (i == MAX_RETRIES - 1) throw e;
                sleepBackoff(i);
            }
        }
        throw new IllegalStateException("unreachable");
    }

    @Transactional
    @Override
    public BankResponse debitAccount(CreditDebitResponse req) {
        BigDecimal amt = Money.normalize(req.amount());

        for (int i = 0; i < MAX_RETRIES; i++) {
            try {
                var acc = repo.findByAccountNumber(req.accountNumber()).orElse(null);
                if (acc == null) return notFound();
                if (!"ACTIVE".equalsIgnoreCase(acc.getStatus())) return blocked("Account is not ACTIVE");

                if (acc.getBalance().compareTo(amt) < 0) return insufficient();

                acc.setBalance(acc.getBalance().subtract(amt));

                tryNotifyAndLogDebit(acc, amt);
                return okDebit(acc);

            } catch (org.springframework.dao.OptimisticLockingFailureException e) {
                if (i == MAX_RETRIES - 1) throw e;
                sleepBackoff(i);
            }
        }
        throw new IllegalStateException("unreachable");
    }

    @Transactional(readOnly = true)
    @Override
    public BankAccount getEntity(String accountNumber) {
        return repo.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Account not found"));
    }

    // ---------- helpers: responses ----------
    private BankResponse notFound() {
        return BankResponse.builder()
                .responseCode(AccountUtils.ACCOUNT_NOT_EXISTS_CODE)
                .responseMessage(AccountUtils.ACCOUNT_NOT_EXISTS_MESSAGE)
                .accountInfo(null)
                .build();
    }

    private BankResponse insufficient() {
        return BankResponse.builder()
                .responseCode(AccountUtils.INSUFFICIENT_BALANCE_CODE)
                .responseMessage(AccountUtils.INSUFFICIENT_BALANCE_MESSAGE)
                .accountInfo(null)
                .build();
    }

    private BankResponse blocked(String reason) {
        return BankResponse.builder()
                .responseCode(AccountUtils.ACCOUNT_BLOCKED_CODE) // define if missing
                .responseMessage(reason != null ? reason : "Account is not ACTIVE")
                .accountInfo(null)
                .build();
    }

    private BankResponse okCredit(BankAccount acc) {
        return BankResponse.builder()
                .responseCode(AccountUtils.ACCOUNT_CREDITED_SUCCESS_CODE)
                .responseMessage(AccountUtils.ACCOUNT_CREDITED_SUCCESS_MESSAGE)
                .accountInfo(AccountInfo.builder()
                        .accountName(acc.getDisplayName())
                        .accountNumber(acc.getAccountNumber())
                        .accountBalance(acc.getBalance())
                        .build())
                .build();
    }

    private BankResponse okDebit(BankAccount acc) {
        return BankResponse.builder()
                .responseCode(AccountUtils.ACCOUNT_DEBITED_SUCCESS_CODE)
                .responseMessage(AccountUtils.ACCOUNT_DEBITED_SUCCESS_MESSAGE)
                .accountInfo(AccountInfo.builder()
                        .accountName(acc.getDisplayName())
                        .accountNumber(acc.getAccountNumber())
                        .accountBalance(acc.getBalance())
                        .build())
                .build();
    }

    // ---------- helpers: best-effort notify + log ----------
    private void tryNotifyAndLogCredit(BankAccount acc, BigDecimal amount) {
        try {
            var profile = profileClient.getByProfileId(acc.getProfileId());
            if (org.springframework.util.StringUtils.hasText(profile.email())) {
                emailService.sendEmailAlert(EmailDetails.builder()
                        .recipient(profile.email())
                        .subject("CREDIT ALERT")
                        .messageBody("The sum of %s SAR has been deposited to your account, %s."
                                .formatted(amount, acc.getDisplayName()))
                        .build());
            }
        } catch (Exception e) {
            log.warn("Credit email failed for {}", acc.getAccountNumber(), e);
        }

        try {
            transactionClient.log(new LogTransactionRequest(
                    acc.getAccountNumber(), "CREDIT", amount, "SUCCESS", LocalDateTime.now()
            ));
        } catch (Exception e) {
            log.warn("Txn log failed for {}", acc.getAccountNumber(), e);
        }
    }

    private void tryNotifyAndLogDebit(BankAccount acc, BigDecimal amount) {
        try {
            var profile = profileClient.getByProfileId(acc.getProfileId());
            if (org.springframework.util.StringUtils.hasText(profile.email())) {
                emailService.sendEmailAlert(EmailDetails.builder()
                        .recipient(profile.email())
                        .subject("DEBIT ALERT")
                        .messageBody("The sum of %s SAR has been deducted from your account, %s."
                                .formatted(amount, acc.getDisplayName()))
                        .build());
            }
        } catch (Exception e) {
            log.warn("Debit email failed for {}", acc.getAccountNumber(), e);
        }

        try {
            transactionClient.log(new LogTransactionRequest(
                    acc.getAccountNumber(), "DEBIT", amount, "SUCCESS", LocalDateTime.now()
            ));
        } catch (Exception e) {
            log.warn("Txn log failed for {}", acc.getAccountNumber(), e);
        }
    }

    private void sleepBackoff(int attempt) {
        try {
            Thread.sleep(50L * (attempt + 1)); // tiny backoff
        } catch (InterruptedException ignored) { Thread.currentThread().interrupt(); }
    }
}