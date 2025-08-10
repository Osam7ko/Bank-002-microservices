package com.osama.bank002.account.client;

import com.osama.bank002.account.client.dto.ProfileSummary;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "PROFILE-SERVICE", url = "${profile.service.url}")
public interface ProfileClient {

    @GetMapping("/api/profiles/id/{profileId}")
    ProfileSummary getByProfileId(@PathVariable("profileId") String profileId);

    @GetMapping("/api/profiles/me")
    ProfileSummary getMyProfile();
}