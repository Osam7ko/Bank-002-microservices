package com.osama.bank002.beneficiary.service;

import com.osama.bank002.beneficiary.client.AccountClient;
import com.osama.bank002.beneficiary.domain.dto.SaveBeneficiaryRequest;
import com.osama.bank002.beneficiary.domain.dto.SavedBeneficiaryDto;
import com.osama.bank002.beneficiary.domain.dto.UserPrincipal;
import com.osama.bank002.beneficiary.domain.entity.SavedBeneficiary;
import com.osama.bank002.beneficiary.repository.BeneficiaryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BeneficiaryServiceImpl implements BeneficiaryService {

    private final BeneficiaryRepository repo;
    private final AccountClient accountClient;

    @Override
    public String currentUserId() {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated())
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Not authenticated");

        if (auth.getPrincipal() instanceof UserPrincipal p) {
            return p.userId();
        }
        throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid principal");
    }

    @Transactional
    @Override
    public SavedBeneficiaryDto save(SaveBeneficiaryRequest req) {
        String uid = currentUserId();

        // idempotency / conflict guard
        repo.findByOwnerUserIdAndAccountNumber(uid, req.accountNumber())
                .ifPresent(b -> {
                    throw new ResponseStatusException(HttpStatus.CONFLICT, "Already saved");
                });

        // Try to resolve recipient name from account-service (best-effort)
        String resolvedName = null;
        try {
            var name = accountClient.name(req.accountNumber());
            if (name != null && !name.isBlank()
                    && !name.equalsIgnoreCase("Account does not exist")) {
                resolvedName = name;
            }
        } catch (Exception ignored) {
        }

        String finalName = (req.beneficiaryName() != null && !req.beneficiaryName().isBlank())
                ? req.beneficiaryName()
                : (resolvedName != null ? resolvedName : "Recipient " + req.accountNumber());

        SavedBeneficiary created = SavedBeneficiary.builder()
                .ownerUserId(uid)
                .beneficiaryName(finalName)
                .accountNumber(req.accountNumber())
                .bankName((req.bankName() == null || req.bankName().isBlank()) ? "001 Bank" : req.bankName())
                .build();

        var saved = repo.save(created);
        return new SavedBeneficiaryDto(saved.getId(), saved.getBeneficiaryName(), saved.getAccountNumber(), saved.getBankName());
    }

    @Transactional(readOnly = true)
    @Override
    public List<SavedBeneficiaryDto> listMine() {
        String uid = currentUserId();
        return repo.findByOwnerUserIdOrderByCreatedAtDesc(uid).stream()
                .map(b -> new SavedBeneficiaryDto(b.getId(), b.getBeneficiaryName(), b.getAccountNumber(), b.getBankName()))
                .toList();
    }

    @Transactional
    @Override
    public void deleteById(Long id) {
        String uid = currentUserId();
        var b = repo.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        if (!uid.equals(b.getOwnerUserId())) throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        repo.delete(b);
    }

    @Override
    @Transactional
    public void deleteByAccountNumber(String accountNumber) {
        String uid = currentUserId();
        var b = repo.findByOwnerUserIdAndAccountNumber(uid, accountNumber)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        repo.delete(b);
    }
}