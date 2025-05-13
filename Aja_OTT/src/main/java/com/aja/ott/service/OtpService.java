package com.aja.ott.service;

import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import com.aja.ott.configuration.OtpStore;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@Service
public class OtpService {
	
	@Autowired
    private OtpStore otpStore;
	
	@Autowired
    private JavaMailSender javaMailSender;
	
	@Autowired
	private EmailService emailService;
	
	
	public void sendOtp(String email) throws MessagingException {
        String otp = String.valueOf(new Random().nextInt(899999) + 100000);
        otpStore.storeOtp(email, otp);
        
        emailService.sendOtpEmail(email, otp);
    }
	
	
	 public boolean verifyOtp(String email, String otp) {
	        boolean isValid = otpStore.verifyOtp(email, otp);
	        if (isValid) {
	            otpStore.markVerified(email);
	        }
	        return isValid;
	    }

	
	 public boolean isEmailVerified(String email) {
	        return otpStore.isVerified(email);
	    }
}
