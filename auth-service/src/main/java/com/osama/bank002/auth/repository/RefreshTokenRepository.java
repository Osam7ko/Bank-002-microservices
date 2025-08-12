package com.osama.bank002.auth.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.osama.bank002.auth.domain.entity.RefreshToken;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, String> {
	Optional<RefreshToken> findByIdAndRevokedFalse(String id);

	@Modifying
	@Query("update RefreshToken r set r.revoked=true where r.userId=:uid")
	int revokeAllForUser(@Param("uid") Long userId); // optional rotation strategy
}