package com.osama.bank002.beneficiary.repository;

import com.osama.bank002.beneficiary.domain.entity.SavedBeneficiary;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface BeneficiaryRepository extends JpaRepository<SavedBeneficiary,Long> {
    List<SavedBeneficiary> findByOwnerUserIdOrderByCreatedAtDesc(String ownerUserId);
    Optional<SavedBeneficiary> findByOwnerUserIdAndAccountNumber(String ownerUserId, String accountNumber);
}