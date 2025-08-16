package com.osama.bank002.profile.service;

import com.osama.bank002.profile.client.AccountClient;
import com.osama.bank002.profile.dto.ProfileCompletedEvent;
import com.osama.bank002.profile.dto.ProfileUpdateRequest;
import com.osama.bank002.profile.entity.Profile;
import com.osama.bank002.profile.mapper.ProfileMapper;
import com.osama.bank002.profile.repository.ProfileRepository;
import com.osama.bank002.profile.util.JwtUtils;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class ProfileServiceImpl implements ProfileService {

    private final ProfileRepository repo;

    private final JwtUtils jwtUtils;
    private final AccountClient accountClient;
    private final ApplicationEventPublisher events;

    @Transactional
    @Override
    public Profile bootstrapIfMissing() {
        String uid = jwtUtils.userIdString();

        return repo.findByUserId(uid).orElseGet(() -> {
            // best-effort: try get email from JWT (if your auth puts it there)
            String email = jwtUtils.email();
            if (email == null || email.isBlank()) {
                // fallback to a temporary address that is syntactically valid
                email = "pending-" + uid + "@example.local";
            }
            return repo.save(Profile.builder()
                    .userId(uid)
                    .firstName("New")
                    .lastName("User")
                    .status("ACTIVE")
                    .email(email)
                    .build());
        });
    }

    @Override
    public Profile getMyProfile() {
        String uid = jwtUtils.userIdString();
        return repo.findByUserId(uid)
                .orElseThrow(() -> new EntityNotFoundException("Profile not found"));
    }

    @Transactional
    @Override
    public Profile updateMyProfile(ProfileUpdateRequest req) {
        Profile p = getMyProfile();
        ProfileMapper.apply(req, p); // write the real data
        // after flush/commit, publish completion
        events.publishEvent(new ProfileCompletedEvent(
                p.getId(),
                (p.getFirstName() + " " + p.getLastName()).trim()
        ));
        return p;
    }

    // Admin-only helper (optional)
    @Override
    public Profile getByUserId(String userId) {
        return repo.findByUserId(userId)
                .orElseThrow(() -> new EntityNotFoundException("Profile not found: " + userId));
    }


    @Transactional
    @Override
    public void softDeleteMyProfile() {
        String uid = jwtUtils.userIdString();
        int openCount = accountClient.countOpenAccounts(uid);
        if (openCount > 0) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "You still have open accounts. Close them before deleting your profile.");
        }
        Profile p = repo.findByUserId(uid)
                .orElseThrow(() -> new EntityNotFoundException("Profile not found"));
        p.setStatus("DELETED");
    }

    @Override
    public Profile getByProfileId(Long profileId) {
        return repo.findById(profileId)
                .orElseThrow(() -> new EntityNotFoundException("Profile not found: " + profileId));
    }
}