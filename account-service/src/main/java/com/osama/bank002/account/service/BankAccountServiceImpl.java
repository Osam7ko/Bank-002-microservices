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
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class BankAccountServiceImpl implements BankAccountService{

    private final BankAccountRepository repo;
    private final EmailService emailService;
    private final ProfileClient profileClient;
    private final TransactionClient transactionClient;

    @Transactional
    @Override
    public BankResponse openAccount(String profileId, String displayName) {
        var profile = profileClient.getByProfileId(profileId);

        String accountNumber = AccountUtils.generateAccountNumber();

        String finalDisplay = (displayName == null || displayName.isBlank())
                ? profile.fullName()
                : displayName;


        BankAccount createdAccount = BankAccount.builder()
                .accountNumber(accountNumber)
                .profileId(profileId)
                .displayName(finalDisplay)
                .balance(BigDecimal.ZERO)
                .status("ACTIVE")
                .build();

        BankAccount savedAccount = repo.save(createdAccount);

        /**
         * Email Configration
         */
        try {
            if (org.springframework.util.StringUtils.hasText(profile.email())) {
                EmailDetails creationAlert = EmailDetails.builder()
                        .recipient(profile.email())
                        .subject("Account creation")
                        .messageBody("""
                Congrats! Your account has been created.
                Account Name: %s
                Account Number: %s
                """.formatted(finalDisplay, accountNumber))
                        .build();
                emailService.sendEmailAlert(creationAlert);
            } else {
                log.warn("Profile {} has no email; skipping creation email", profileId);
            }
        } catch (Exception e) {
            // swallow so the transaction isn’t rolled back just because email failed
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

    @Override
    @Transactional(readOnly = true)
    public BankResponse balanceEnquiry(EnquiryRequest req) {
        var accountOperations = repo.findByAccountNumber(req.accountNumber());
        if(accountOperations.isEmpty()){
            return BankResponse.builder()
                    .responseCode(AccountUtils.ACCOUNT_NOT_EXISTS_CODE)
                    .responseMessage(AccountUtils.ACCOUNT_NOT_EXISTS_MESSAGE)
                    .accountInfo(null)
                    .build();
        }

        BankAccount account = accountOperations.get();
        return BankResponse.builder()
                .responseCode(AccountUtils.ACCOUNT_FOUND_CODE)
                .responseMessage(AccountUtils.ACCOUNT_FOUND_SUCCESS)
                .accountInfo(AccountInfo.builder()
                        .accountName(account.getDisplayName())
                        .accountNumber(account.getAccountNumber())
                        .accountBalance(account.getBalance())
                        .build())
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public String nameEnquiry(EnquiryRequest req) {
        return repo.findByAccountNumber(req.accountNumber())
                .map(a -> (a.getDisplayName() == null || a.getDisplayName().isBlank())
                        ? ("Account " + a.getAccountNumber())
                        : a.getDisplayName())
                .orElse(AccountUtils.ACCOUNT_NOT_EXISTS_MESSAGE);
    }

    @Override
    @Transactional
    public BankResponse creditAccount(CreditDebitResponse req) {

        BankAccount accountOperations = repo.findByAccountNumber(req.accountNumber())
                .orElse(null);
        if (accountOperations == null) {
            return BankResponse.builder()
                    .responseCode(AccountUtils.ACCOUNT_NOT_EXISTS_CODE)
                    .responseMessage(AccountUtils.ACCOUNT_NOT_EXISTS_MESSAGE)
                    .accountInfo(null)
                    .build();
        }

        accountOperations.setBalance(accountOperations.getBalance().add(Money.normalize(req.amount())));
        // transactionClient.log(acc.getAccountNumber(), "CREDIT", req.amount()); // optional

        /**
         * Email Configration
         */
        var profile = profileClient.getByProfileId(accountOperations.getProfileId());
        EmailDetails creationAlert = EmailDetails.builder()
                .recipient(profile.email())
                .subject("CREDIT ALERT")
                .messageBody("The sum of %s SAR has been deposited to your account, %s."
                        .formatted(req.amount(), accountOperations.getDisplayName()))
                .build();
        emailService.sendEmailAlert(creationAlert);

        // Transaction Alert
        transactionClient.log(new LogTransactionRequest(
                accountOperations.getAccountNumber(), "CREDIT", req.amount(), "SUCCESS", LocalDateTime.now()
        ));

        return BankResponse.builder()
                .responseCode(AccountUtils.ACCOUNT_CREDITED_SUCCESS_CODE)
                .responseMessage(AccountUtils.ACCOUNT_CREDITED_SUCCESS_MESSAGE)
                .accountInfo(AccountInfo.builder()
                        .accountName(accountOperations.getDisplayName())
                        .accountNumber(accountOperations.getAccountNumber())
                        .accountBalance(accountOperations.getBalance())
                        .build())
                .build();
    }

    @Override
    @Transactional
    public BankResponse debitAccount(CreditDebitResponse req) {
        BankAccount accountOperations = repo.findByAccountNumber(req.accountNumber())
                .orElse(null);
        if (accountOperations == null) {
            return BankResponse.builder()
                    .responseCode(AccountUtils.ACCOUNT_NOT_EXISTS_CODE)
                    .responseMessage(AccountUtils.ACCOUNT_NOT_EXISTS_MESSAGE)
                    .accountInfo(null)
                    .build();
        }

        BigDecimal amt = Money.normalize(req.amount());
        if (accountOperations.getBalance().compareTo(amt) < 0) {
            return BankResponse.builder()
                    .responseCode(AccountUtils.INSUFFICIENT_BALANCE_CODE)
                    .responseMessage(AccountUtils.INSUFFICIENT_BALANCE_MESSAGE)
                    .accountInfo(null)
                    .build();
        }

        accountOperations.setBalance(accountOperations.getBalance().subtract(amt));


        /**
         * Email Configration
         */
        var profile = profileClient.getByProfileId(accountOperations.getProfileId());
        EmailDetails creationAlert = EmailDetails.builder()
                .recipient(profile.email())
                .subject("DEBIT ALERT")
                .messageBody("The sum of %s SAR has been deducted from your account, %s."
                        .formatted(req.amount(), accountOperations.getDisplayName()))
                .build();
        emailService.sendEmailAlert(creationAlert);

        // Transaction Alert
        transactionClient.log(new LogTransactionRequest(
                accountOperations.getAccountNumber(), "DEBIT", req.amount(), "SUCCESS", LocalDateTime.now()
        ));



        return BankResponse.builder()
                .responseCode(AccountUtils.ACCOUNT_DEBITED_SUCCESS_CODE)
                .responseMessage(AccountUtils.ACCOUNT_DEBITED_SUCCESS_MESSAGE)
                .accountInfo(AccountInfo.builder()
                        .accountName(accountOperations.getDisplayName())
                        .accountNumber(accountOperations.getAccountNumber())
                        .accountBalance(accountOperations.getBalance())
                        .build())
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public BankAccount getEntity(String accountNumber) {
        return repo.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Account not found"));
    }
}