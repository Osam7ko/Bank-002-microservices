package com.osama.bank002.profile.service;

import com.osama.bank002.profile.dto.ProfileUpdateRequest;
import com.osama.bank002.profile.entity.Profile;
import com.osama.bank002.profile.repository.ProfileRepository;
import com.osama.bank002.profile.util.JwtUtils;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProfileServiceImpl implements ProfileService {

    private final ProfileRepository repo;

    private final JwtUtils jwtUtils;

    @Transactional
    @Override
    public Profile bootstrapIfMissing(Jwt jwt) {
        String uid = jwtUtils.userId(jwt);
        return repo.findByUserId(uid).orElseGet(() -> {
            Profile p = Profile.builder()
                    .userId(uid)
                    .firstName("New")
                    .lastName("User")
                    .status("ACTIVE")
                    .build();
            return repo.save(p);
        });
    }

    @Override
    public Profile getMyProfile(Jwt jwt) {
        return repo.findByUserId(jwtUtils.userId(jwt))
                .orElseThrow(() -> new EntityNotFoundException("Profile not found"));
    }

    @Transactional
    @Override
    public Profile updateMyProfile(Jwt jwt, ProfileUpdateRequest req) {
        Profile p = getMyProfile(jwt);
        // optimistic locking via @Version if concurrent updates happen
        com.osama.bank002.profile.mapper.ProfileMapper.apply(req, p);
        return p; // dirty checking persists changes
    }

    // Admin-only helper (optional)
    @Override
    public Profile getByUserId(String userId) {
        return repo.findByUserId(userId)
                .orElseThrow(() -> new EntityNotFoundException("Profile not found: " + userId));
    }


    @Transactional
    @Override
    public void softDeleteMyProfile(Jwt jwt) {
        var uid = jwtUtils.userId(jwt);
        // TODO: call account-service: GET /api/accounts/owner/{userId}/count
//        int openCount = accountClient.countOpenAccounts(uid);
//        if (openCount > 0) {
//            throw new ResponseStatusException(HttpStatus.CONFLICT,
//                    "You still have open accounts. Close them before deleting your profile.");
//        }
        Profile p = repo.findByUserId(uid)
                .orElseThrow(() -> new EntityNotFoundException("Profile not found"));
        p.setStatus("DELETED");
        // Optional: scrub PII if you must comply with erasure
        // p.setFirstName("DELETED"); p.setPhoneNumber(null); ...
    }

}