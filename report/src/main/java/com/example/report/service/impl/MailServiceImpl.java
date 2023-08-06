package com.example.report.service.impl;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.example.report.service.MailService;

@Service
public class MailServiceImpl implements MailService {

	private Logger loggerSendMail = LoggerFactory.getLogger(this.getClass());

	@Value("${mail.smtp.host}")
	private String smtpHost;

	@Value("${mail.smtp.port}")
	private String smtpPort;

	@Value("${mail.smtp.user}")
	private String smtpUser;

	@Value("${mail.smtp.password}")
	private String smtpPassword;

	@Value("${mail.recipient.cc}")
	private String recipientEmailCC;

	public Session connectSMTP(String smtpHost, String smtpPort, String smtpUser, String smtpPassword) {

		// Search MAIL_NOTIFY
		loggerSendMail.info("############################ connectSMTP...{}", smtpHost);
		Session sessionMail = null;
		try {

			loggerSendMail.info("CONNECTION...");

			Properties props = System.getProperties();

			props.put("mail.smtp.host", smtpHost);
			props.put("mail.smtp.port", smtpPort);
			props.put("mail.smtp.auth", "true");
			props.put("mail.smtp.starttls.enable", "true");

			sessionMail = Session.getInstance(props, new Authenticator() {

				protected PasswordAuthentication getPasswordAuthentication() {
					return new PasswordAuthentication(smtpUser, smtpPassword);// Specify the Username and the PassWord
				}

			});

			loggerSendMail.info("SMTP LOGIN");
		} catch (Exception e) {
			loggerSendMail.info("ERROR CONNECT SMTP: " + e.getMessage());
		}
		return sessionMail;
	}

	public Session connectDefaultSMTP() {
		return connectSMTP(this.smtpHost, this.smtpPort, this.smtpUser, this.smtpPassword);
	}

	public void send(Session session, String senderEmail, String senderName, String recipientEmail,
			String mailSubject,
			String mailBody,
			File... attachFile) throws IOException {

		try {

			loggerSendMail.info(">>>> Send mail notify to : {}", recipientEmail);

			Message msg = new MimeMessage(session);
			// set message headers
			msg.addHeader("Content-type", "text/HTML; charset=UTF-8");
			msg.addHeader("format", "flowed");
			msg.addHeader("Content-Transfer-Encoding", "8bit");

			msg.setFrom(new InternetAddress(senderEmail, senderName));

			msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipientEmail));
			msg.setRecipients(Message.RecipientType.CC, InternetAddress.parse(recipientEmailCC));
			msg.setSubject(mailSubject);
			msg.setSentDate(new Date());

			MimeBodyPart messageBodyPart = new MimeBodyPart();

			Multipart multipart = new MimeMultipart();

			messageBodyPart.setText(mailBody);

			multipart.addBodyPart(messageBodyPart);
			if (attachFile != null && attachFile.length > 0) {
				for (File attach : attachFile) {
					MimeBodyPart attachPart = new MimeBodyPart();
					attachPart.attachFile(attach);
					multipart.addBodyPart(attachPart);
				}
			}

			msg.setContent(multipart);
			Transport.send(msg);
			loggerSendMail.info("Sending mail notify success");

		} catch (MessagingException e) {
			e.printStackTrace();
		}

	}
}
