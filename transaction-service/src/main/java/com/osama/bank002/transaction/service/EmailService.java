package com.osama.bank002.transaction.service;

import java.io.File;

public interface EmailService {
    void sendEmailWithAttachment(String to, String subject, String body, File attachment);
}