package com.aja.ott.service;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;
@Slf4j
@Service
public class EmailService {
	
	
	@Value("${spring.mail.username}")
	private String senderEmail;

	@Autowired
	private final JavaMailSender javaMailSender;

	public EmailService(JavaMailSender mailSender) {
		this.javaMailSender = mailSender;
	}
	
	public void sendEmail(String recipient, String subject, String body) {
        validateInputs(recipient, subject, body);

        try {
            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            helper.setFrom(senderEmail);
            helper.setTo(recipient);
            helper.setSubject(subject);
            helper.setText(body, true);

            javaMailSender.send(message);
            log.info("✅ Email sent successfully to: {}", recipient);

        } catch (MessagingException | MailException e) {
            log.error("❌ Failed to send email to {}: {}", recipient, e.getMessage(), e);
        }
    }	
	private void validateInputs(String recipient, String subject, String body) {
        if (recipient == null || recipient.trim().isEmpty()) {
            throw new IllegalArgumentException("Recipient email must not be null or empty.");
        }
        if (subject == null || subject.trim().isEmpty()) {
            throw new IllegalArgumentException("Subject must not be null or empty.");
        }
        if (body == null || body.trim().isEmpty()) {
            throw new IllegalArgumentException("Email body must not be null or empty.");
        }
        if (senderEmail == null || senderEmail.trim().isEmpty()) {
            throw new IllegalArgumentException("Sender email is not configured.");
        }
    }

	
	public void sendOtpEmail(String toEmail, String otp) throws MessagingException{
		MimeMessage message = javaMailSender.createMimeMessage();
		MimeMessageHelper helper = new MimeMessageHelper(message,true);
        helper.setFrom(new InternetAddress("no_reply@ajacs.in"));
        helper.setTo(toEmail);
        helper.setSubject("Your OTP Code");
        helper.setText("Your OTP is: " + otp);
        javaMailSender.send(message);
        
        System.out.println("OTP has sent successfully to: " + toEmail);
    }
}
