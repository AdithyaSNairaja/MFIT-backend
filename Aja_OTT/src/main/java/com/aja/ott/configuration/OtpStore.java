package com.aja.ott.configuration;

import java.util.HashMap; 
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.springframework.stereotype.Component;

@Component
public class OtpStore {
    private final Map<String, String> otpMap = new HashMap<>();
    private final Set<String> verifiedEmails = new HashSet<>();

    public void storeOtp(String email, String otp) {
        otpMap.put(email, otp);
    }

    public boolean verifyOtp(String email, String otp) {
        return otp.equals(otpMap.get(email));
    }

    public void markVerified(String email) {
        verifiedEmails.add(email);
        otpMap.remove(email);
    }

    public boolean isVerified(String email) {
        return verifiedEmails.contains(email);
    }

    public void clear(String email) {
        otpMap.remove(email);
        verifiedEmails.remove(email);
    }
}
