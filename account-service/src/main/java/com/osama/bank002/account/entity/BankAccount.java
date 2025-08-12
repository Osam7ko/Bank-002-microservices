package com.osama.bank002.account.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "bank_accounts",
        uniqueConstraints = @UniqueConstraint(name="uk_account_number", columnNames="account_number"),
        indexes = @Index(name="idx_accounts_profile", columnList="profile_id"))
public class BankAccount {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String accountNumber;

    @Column(name="profile_id", nullable=false, length=64)
    private String profileId;  // or ownerUserId

    @Column(name="display_name", length=180)
    private String displayName; // snapshot from profile-service at open time

    @Column(name="balance", nullable=false, precision=19, scale=2)
    private BigDecimal balance;

    @Column(name="status", nullable=false, length=20)
    private String status; // ACTIVE,FROZEN,CLOSED

    @CreationTimestamp
    @Column(name="created_at", updatable=false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name="modified_at")
    private LocalDateTime modifiedAt;

    @Version
    private Long version;

}