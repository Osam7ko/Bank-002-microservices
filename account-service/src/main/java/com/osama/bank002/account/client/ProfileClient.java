package com.osama.bank002.account.client;

import com.osama.bank002.account.client.dto.ProfileSummary;
import com.osama.bank002.account.config.FeignAuthConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "PROFILE-SERVICE", configuration = FeignAuthConfig.class)
public interface ProfileClient {

    @GetMapping("/api/profiles/id/{profileId}")
    ProfileSummary getByProfileId(@PathVariable("profileId") String profileId);

}