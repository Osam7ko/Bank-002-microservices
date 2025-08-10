package com.osama.bank002.transfer.domain.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "transfers", uniqueConstraints = {
        @UniqueConstraint(name = "uk_transfer_idem", columnNames = {"requesterUserId", "idempotencyKey"})
})
public class Transfer {

    @Id
    private String id;

    private String requesterUserId;
    private String fromAccount;
    private String toAccount;

    @Column(precision = 19, scale = 2)
    private BigDecimal amount;
    private String status; // PENDING, SUCCESS, FAILED
    private String idempotencyKey;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}