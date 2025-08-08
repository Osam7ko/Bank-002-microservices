package com.osama.bank002.profile.repository;

import com.osama.bank002.profile.entity.Profile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProfileRepository extends JpaRepository<Profile, Long> {
    Optional<Profile> findByUserId(String userId);

    boolean existsByUserId(String userId);
}