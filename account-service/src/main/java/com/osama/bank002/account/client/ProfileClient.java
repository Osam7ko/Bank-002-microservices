package com.osama.bank002.account.client;

import com.osama.bank002.account.client.dto.ProfileSummary;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "PROFILE-SERVICE")
public interface ProfileClient {

    @GetMapping("/api/profiles/{profileId}")
    ProfileSummary getByProfileId(@PathVariable String profileId);

    @GetMapping("/api/profiles/me")
    ProfileSummary getMyProfile();
}