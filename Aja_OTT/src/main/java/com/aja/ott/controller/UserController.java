package com.aja.ott.controller;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.aja.ott.entity.User;
import com.aja.ott.service.EmailService;
import com.aja.ott.service.OtpService;
import com.aja.ott.service.UserService;

import jakarta.mail.MessagingException;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/user")
@CrossOrigin(origins = "http://localhost:5173")
public class UserController {

	@Autowired
	private PasswordEncoder passwordEncoder;
	
	@Autowired
	private OtpService otpService;
	
	@Autowired
	private EmailService emailService;
	
	@Autowired
	private final UserService userService;

	public UserController(UserService userService) {
		this.userService = userService;
	}

	@PostMapping("/save-user")
	public ResponseEntity<String> saveUser(@RequestBody User user) {
		 if (!otpService.isEmailVerified(user.getEmail())) {
	            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Email not verified");
	        }
		try {
			log.info("Saving user");
			userService.saveUser(user);
			log.info("User registered successfully.");
			return ResponseEntity.ok("User Registered successfully");
		} catch (Exception e) {
			log.error("Error registering user: {}", e);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body("Error registering user: " + e.getMessage());
		}
	}

	@GetMapping("/get-all-users")
	public List<User> getAllUsers() {
		log.info("Fetching all users.");
		return userService.getAll();
	}
	
	 @GetMapping("/get-all-hr")
	    public List<User> getHRUsers() {
	        return userService.getAllHRUsers();
	    }

	@GetMapping("/get-user-by-id/{id}")
	public ResponseEntity<User> getUserById(@PathVariable Long id) {
		log.info("Fetching user by ID: {}", id);
		Optional<User> user = userService.detailsBasedOnId(id);
		return user.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
	}

	@PutMapping("/update-user/{id}")
	public ResponseEntity<String> updateUser(@PathVariable Long id, @RequestBody User user) {
		log.info("Updating user with ID: {}", id);
		String result="";
		try {
			result = userService.updateUser(id, user);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		log.info("User updated: {}", result);
		return ResponseEntity.ok(result);
	}

	@DeleteMapping("/delete-user/{id}")
	public ResponseEntity<String> deleteUser(@PathVariable Long id) {
		log.info("Deleting (soft) user with ID: {}", id);
		boolean deleted = userService.softDeleteUser(id);
		if (deleted) {
			log.info("User with ID {} soft deleted.", id);
			return ResponseEntity.ok("User soft deleted successfully.");
		} else {
			log.warn("User with ID {} not found or already deleted.", id);
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found or already deleted.");
		}
	}

	@PostMapping("/login")
	public Map<String, String> login(@RequestBody User user) {
		log.info("Login attempt for user: {}", user);
		return userService.verify(user);
	}

	@PostMapping("/send-otp")
	public ResponseEntity<String> sendOTP(@RequestBody Map<String, String> requestBody) {
		String email = requestBody.get("email");
		log.info("Sending OTP to email: {}", email);
		if (email == null || email.isEmpty()) {
			log.warn("Email is missing in OTP request.");
			return ResponseEntity.badRequest().body("Email is required");
		}

		boolean otpSent = userService.sendOTP(email);

		if (otpSent) {
			log.info("OTP sent successfully to: {}", email);
			return ResponseEntity.ok("OTP sent to " + email);
		} else {
			log.warn("OTP not sent. User not registered with email: {}", email);
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not registered with this email");
		}
	}

	@PostMapping("/verify-otp")
	public ResponseEntity<String> verifyOTP(@RequestBody Map<String, String> requestBody) {
		String email = requestBody.get("email");
		String otp = requestBody.get("otp");
		log.info("Verifying OTP for email: {}", email);

		if (email == null || email.isEmpty() || otp == null || otp.isEmpty()) {
			log.warn("Email or OTP is missing in verification request.");
			return ResponseEntity.badRequest().body("Email and OTP are required");
		}

		boolean otpVerified = userService.verifyOTP(email, otp);
		if (otpVerified) {
			log.info("OTP verified successfully for email: {}", email);
			return ResponseEntity.ok("OTP verified successfully. You can now reset your password.");
		} else {
			log.warn("Invalid OTP for email: {}", email);
			return ResponseEntity.status(400).body("Invalid OTP");
		}
	}

	@PostMapping("/reset-password")
	public ResponseEntity<String> resetPassword(@RequestBody Map<String, String> requestBody) {
		try {
			String email = requestBody.get("email");
			String newPassword = requestBody.get("newPassword");
			String confirmPassword = requestBody.get("confirmPassword");

			log.info("Resetting password for email: {}", email);

			if (email == null || email.isEmpty() || newPassword == null || newPassword.isEmpty()
					|| confirmPassword == null || confirmPassword.isEmpty()) {
				log.warn("Missing input fields for password reset.");
				return ResponseEntity.badRequest().body("Email, new password, and confirm password are required");
			}

			if (!newPassword.equals(confirmPassword)) {
				log.warn("New password and confirm password do not match for email: {}", email);
				return ResponseEntity.badRequest().body("New password and confirm password do not match.");
			}

			if (passwordEncoder == null) {
				log.error("Password encoder is null.");
				return ResponseEntity.status(500).body("Internal Server Error: Password encoder is null.");
			}

			String hashedPassword = passwordEncoder.encode(newPassword);

			String normalizedEmail = email.trim().toLowerCase();

			boolean passwordReset = userService.resetPassword(normalizedEmail, hashedPassword);

			if (passwordReset) {
				log.info("Password reset successfully for email: {}", email);
				return ResponseEntity.ok("Password reset successfully.");
			} else {
				log.warn("User not found for password reset: {}", email);
				return ResponseEntity.status(404).body("User not found.");
			}
		} catch (Exception e) {
			log.error("Error during password reset: {}", e.getMessage());
			return ResponseEntity.status(500).body("Internal Server Error: " + e.getMessage());
		}
	}  
  
	@PutMapping("/change-password")
	 public ResponseEntity<Map<String, String>> changePassword(
	         @RequestBody Map<String, String> passwordData,
	         @RequestHeader("Authorization") String token) {

	     if (token != null && token.startsWith("Bearer ")) {
	         token = token.substring(7).trim();
	     } else {
	         return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Collections.singletonMap("error", "Authorization token is missing or malformed"));
	     }

	     String oldPassword = passwordData.get("oldPassword");
	     String newPassword = passwordData.get("newPassword");
	     String confirmPassword = passwordData.get("confirmPassword");

	     Map<String, String> response = userService.changePassword(oldPassword, newPassword, confirmPassword, token);

	     if (response.containsKey("error")) {
	         return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
	     }

	     return ResponseEntity.status(HttpStatus.OK).body(response);
	 }
	

    @PostMapping("/send-user-otp")
    public ResponseEntity<String> sendOtp(@RequestBody Map<String, String> payload) {
        String email = payload.get("email");
        try {
			otpService.sendOtp(email);
		} catch (MessagingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        return ResponseEntity.ok("OTP sent to " + email);
    }
	

    @PostMapping("/verify-user-otp")
    public ResponseEntity<String> verifyOtp(@RequestBody Map<String, String> payload) {
        String email = payload.get("email");
        String otp = payload.get("otp");

        if (otpService.verifyOtp(email, otp)) {
            return ResponseEntity.ok("OTP verified");
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid OTP");
        }
    }

}
