package com.osama.bank002.auth.domain.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "auth_refresh_tokens")
@Getter
@Setter
public class RefreshToken {
	@Id
	private String id; // UUID
	@Column(nullable = false)
	private Long userId;
	@Column(nullable = false)
	private LocalDateTime expiresAt;
	private boolean revoked;
	private LocalDateTime createdAt;
}
