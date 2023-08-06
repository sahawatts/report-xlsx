package com.example.report.service;

import java.io.File;
import java.io.IOException;

import javax.mail.Session;

public interface MailService {

    Session connectSMTP(String smtpHost, String smtpPort, String smtpUser, String smtpPassword);

    Session connectDefaultSMTP();

    void send(Session session, String senderEmail, String senderName, String recipientEmail, String mailSubject,
            String mailBody, File... attachFile) throws IOException;

}
