package com.osama.bank002.card.domain.entity;

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
@Table(name = "bank_cards", indexes = {
        @Index(name = "idx_card_account", columnList = "accountNumber"),
        @Index(name = "idx_card_pan_unique", columnList = "cardNumber", unique = true)
})
public class BankCard {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 19)
    private String cardNumber;          // PAN (store, but mask on any response/log!)

    @Column(nullable = false)
    private String accountNumber;       // reference to ACCOUNT-SERVICE

    @Column(nullable = false, length = 4)
    private String expiryMonth;         // "01".."12"

    @Column(nullable = false, length = 4)
    private String expiryYear;          // "2028"

    @Column(nullable = false)
    private String cvvHash;             // NEVER store raw CVV

    @Column(nullable = false)
    private String cardType;            // VISA, MASTER, MADA

    @Column(nullable = false)
    private String status;              // ACTIVE, BLOCKED, EXPIRED

    @Column(nullable = false)
    private String signature;           // bank signature (HMAC over (PAN|exp|account))

    private String nameOnCard;          // optional emboss name

    @CreationTimestamp
    private LocalDateTime createdAt;
    @UpdateTimestamp
    private LocalDateTime updatedAt;
}