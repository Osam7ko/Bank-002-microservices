package com.osama.bank002.beneficiary.service;

import com.osama.bank002.beneficiary.client.AccountClient;
import com.osama.bank002.beneficiary.domain.dto.SaveBeneficiaryRequest;
import com.osama.bank002.beneficiary.domain.dto.SavedBeneficiaryDto;
import com.osama.bank002.beneficiary.domain.entity.SavedBeneficiary;
import com.osama.bank002.beneficiary.repository.BeneficiaryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
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
        var auth = (JwtAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
        return auth.getToken().getSubject();
    }

    @Transactional
    @Override
    public SavedBeneficiaryDto save(SaveBeneficiaryRequest req) {
        String uid = currentUserId();

        try {
            String resolved = accountClient.name(req.accountNumber());
            if (resolved != null && !resolved.isBlank()
                    && !resolved.equalsIgnoreCase("Account does not exist")) {

            }
        } catch (Exception ignored) {
        }

        if (repo.findByOwnerUserIdAndAccountNumber(uid, req.accountNumber()).isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Already saved");
        }

        var createdSavedBeneficiary = SavedBeneficiary.builder()
                .ownerUserId(uid)
                .beneficiaryName(req.beneficiaryName())
                .accountNumber(req.accountNumber())
                .bankName(req.bankName())
                .build();

        var saved = repo.save(createdSavedBeneficiary);
        return new SavedBeneficiaryDto(saved.getId(), saved.getBeneficiaryName(), saved.getAccountNumber(), saved.getBankName());
    }

    @Transactional(readOnly = true)
    @Override
    public List<SavedBeneficiaryDto> listMine() {
        String uid = currentUserId();
        return repo.findByOwnerUserIdOrderByCreatedAtDesc(uid).stream()
                .map(
                        b -> new SavedBeneficiaryDto(b.getId(), b.getBeneficiaryName(), b.getAccountNumber(), b.getBankName())

                )
                .toList();
    }

    @Transactional
    @Override
    public void deleteById(Long id) {
        String uid = currentUserId();
        var b = repo.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        if (!b.getOwnerUserId().equals(uid)) throw new ResponseStatusException(HttpStatus.FORBIDDEN);
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