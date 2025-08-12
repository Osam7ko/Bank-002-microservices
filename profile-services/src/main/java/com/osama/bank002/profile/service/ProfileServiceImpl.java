package com.osama.bank002.profile.service;

import com.osama.bank002.profile.client.AccountClient;
import com.osama.bank002.profile.client.dto.OpenAccountRequest;
import com.osama.bank002.profile.dto.ProfileUpdateRequest;
import com.osama.bank002.profile.entity.Profile;
import com.osama.bank002.profile.repository.ProfileRepository;
import com.osama.bank002.profile.util.JwtUtils;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class ProfileServiceImpl implements ProfileService {

    private final ProfileRepository repo;

    private final JwtUtils jwtUtils;
    private final AccountClient accountClient;

    @Transactional
    @Override
    public Profile bootstrapIfMissing() {
        String uid = jwtUtils.userIdString();   // now from our filter
        Profile p = repo.findByUserId(uid).orElseGet(() ->
                repo.save(Profile.builder()
                        .userId(uid).firstName("New").lastName("User").status("ACTIVE").build())
        );

        // open default account ONCE
        int count = accountClient.countOpenAccounts(p.getId().toString());
        if (count == 0) {
            String displayName = (p.getFirstName() + " " + p.getLastName()).trim();
            accountClient.open(new OpenAccountRequest(p.getId().toString(), displayName));
        }
        return p;
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
        com.osama.bank002.profile.mapper.ProfileMapper.apply(req, p);
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