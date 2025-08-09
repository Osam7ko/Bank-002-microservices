package com.osama.bank002.account.service;

import com.osama.bank002.account.dto.EmailDetails;

public interface EmailService {
    boolean sendEmailAlert(EmailDetails emailDetails);

    // send attachment
    void sendEmailWithAttachment(EmailDetails emailDetails);

}