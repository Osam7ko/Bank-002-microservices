package com.osama.bank002.transfer.repository;

import com.osama.bank002.transfer.domain.entity.Transfer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TransferRepository extends JpaRepository<Transfer, String> {
    Optional<Transfer> findByRequesterUserIdAndIdempotencyKey(String requesterUserId, String idempotencyKey);
}