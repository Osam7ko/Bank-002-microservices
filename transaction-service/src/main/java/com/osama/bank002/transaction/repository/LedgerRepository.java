package com.osama.bank002.transaction.repository;

import com.osama.bank002.transaction.domain.entity.LedgerEntry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface LedgerRepository extends JpaRepository<LedgerEntry, String> {
    @Query("select l from LedgerEntry l where l.accountNumber=:acc and l.createdAt between :from and :to order by l.createdAt desc")
    List<LedgerEntry> findRange(@Param("acc") String accountNumber,
                                @Param("from") LocalDateTime from,
                                @Param("to") LocalDateTime to);
}