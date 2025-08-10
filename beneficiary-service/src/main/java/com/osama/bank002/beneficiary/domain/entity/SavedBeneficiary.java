package com.osama.bank002.beneficiary.domain.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "saved_beneficiaries",
        uniqueConstraints = @UniqueConstraint(name="uk_owner_account",
                columnNames = {"owner_user_id","account_number"}))
public class SavedBeneficiary {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "owner_user_id", nullable = false)
    private String ownerUserId;  // from Keycloak token (sub)

    @Column(nullable = false)
    private String beneficiaryName;

    @Column(name = "account_number", nullable = false)
    private String accountNumber;

    private String bankName;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime modifiedAt;
}