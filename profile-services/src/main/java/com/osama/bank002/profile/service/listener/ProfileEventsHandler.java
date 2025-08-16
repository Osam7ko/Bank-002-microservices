package com.osama.bank002.profile.service.listener;

import com.osama.bank002.profile.client.AccountClient;
import com.osama.bank002.profile.client.dto.OpenAccountRequest;
import com.osama.bank002.profile.dto.ProfileCompletedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class ProfileEventsHandler {

    private final AccountClient accountClient;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onCompleted(ProfileCompletedEvent e) {
        try {
            int count = accountClient.countOpenAccounts(e.profileId().toString());
            if (count == 0) {
                accountClient.open(new OpenAccountRequest(
                        e.profileId().toString(),
                        e.displayName()
                ));
            }
        } catch (Exception ex) {
            // log and swallow; FE can expose a “Retry open account” button if desired
            // log.warn("Open default account failed for profile {}", e.profileId(), ex);
        }
    }
}