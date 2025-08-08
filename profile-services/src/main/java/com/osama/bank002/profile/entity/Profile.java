package com.osama.bank002.profile.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
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
@Table(name = "profiles",
        uniqueConstraints = @UniqueConstraint(name = "uk_profiles_user_id", columnNames = "user_id"),
        indexes = {
                @Index(name = "idx_profiles_last_first", columnList = "last_name, first_name"),
                @Index(name = "idx_profiles_phone", columnList = "phone_number")
        })
public class Profile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Keycloak user id
     */
    @Column(name = "user_id", nullable = false, updatable = false, length = 64)
    private String userId;

    @NotBlank
    @Size(max = 60)
    @Column(name = "first_name", nullable = false)
    private String firstName;

    @NotBlank
    @Size(max = 60)
    @Column(name = "last_name", nullable = false)
    private String lastName;

    @Size(max = 60)
    @Column(name = "other_name")
    private String otherName;

    @Size(max = 20)
    @Column(name = "gender")
    private String gender;

    @Size(max = 120)
    @Column(name = "address")
    private String address;

    @Size(max = 60)
    @Column(name = "state_of_origin")
    private String stateOfOrigin;

    @Size(max = 32)
    @Column(name = "phone_number")
    private String phoneNumber;

    @Size(max = 32)
    @Column(name = "alt_phone_number")
    private String alternativePhoneNumber;

    @Size(max = 20)
    @Column(name = "status")
    private String status;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "modified_at", nullable = false)
    private LocalDateTime modifiedAt;

    @Version
    private Long version;

}