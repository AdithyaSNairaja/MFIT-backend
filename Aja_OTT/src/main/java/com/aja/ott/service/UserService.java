package com.aja.ott.service;

import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.dao.DataAccessException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.aja.ott.configuration.JwtFilter;
import com.aja.ott.entity.User;
import com.aja.ott.exception.CustomDatabaseException;
import com.aja.ott.exception.UserAlreadyExistsException;
import com.aja.ott.repository.UserRepository;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class UserService {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private JwtFilter jwtFilter;

    @Autowired
    private EmailService emailService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JavaMailSender javaMailSender;
    
	@Autowired
	private UserRepository userRepo;
 

    private Map<String, String> otpStore = new HashMap<>();
    private Map<String, Long> otpTimestamps = new HashMap<>();

    private final String FROM_EMAIL = "no_reply@ajacs.in";

    public User saveUser(User user) throws IOException {
        log.info("Attempting to save user with email: {}", user.getEmail());
        if (userRepository.findByEmail(user.getEmail()) != null) {
            log.warn("User already exists with email: {}", user.getEmail());
            throw new UserAlreadyExistsException("User has already been registered with this email.");
        }
        if (userRepository.existsByPhone(user.getPhone())) {
            log.warn("User already exists with phone: {}", user.getPhone());
            throw new UserAlreadyExistsException("User has already been registered with this phone number.");
        }

        ClassPathResource resource = new ClassPathResource("static/userRegistration.html");
        String htmlContent = new String(Files.readAllBytes(resource.getFile().toPath()), StandardCharsets.UTF_8);
        htmlContent = htmlContent.replace("${firstName}", user.getFirstName())
                                 .replace("${password}", user.getPassword())
                                 .replace("${lastName}", user.getLastName());

        emailService.sendEmail(user.getEmail(), "Registration", htmlContent);

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        try {
            User savedUser = userRepository.save(user);
            log.info("User saved successfully with ID: {}", savedUser.getId());
            return savedUser;
        } catch (DataAccessException ex) {
            log.error("Database error occurred", ex);
            throw new CustomDatabaseException("Unable to save the User");
        }
    }
    public List<User> getAllHRUsers() {
        return userRepository.findByRoleAndIsDeletedFalse("HR");
    }

    public List<User> getAll() {
        try {
            log.info("Fetching all non-deleted users.");
            return userRepository.findByIsDeletedFalse();
        } catch (DataAccessException ex) {
            log.error("Database error occurred", ex);
            throw new CustomDatabaseException("Unable to get the User");
        }
    }

    public Optional<User> detailsBasedOnId(Long id) {
        log.info("Fetching user details for ID: {}", id);
        return userRepository.findById(id);
    }

    public boolean softDeleteUser(Long id) {
        log.info("Attempting soft delete for user with ID: {}", id);
        Optional<User> userOptional = userRepository.findById(id);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            if (user.isDeleted()) {
                log.warn("User with ID {} is already marked as deleted.", id);
                return false;
            }
            user.setDeleted(true);
            try {
                userRepository.save(user);
                log.info("User with ID {} soft deleted successfully.", id);
                return true;
            } catch (DataAccessException ex) {
                log.error("Database error occurred", ex);
                throw new CustomDatabaseException("Unable to delete");
            }
        }
        log.warn("User with ID {} not found.", id);
        return false;
    }

    private Map<String, String> compareAndGetUpdatedFields(User existingUser, User updatedUser) {
        Map<String, String> updatedFields = new LinkedHashMap<>();
        if (!Objects.equals(existingUser.getFirstName(), updatedUser.getFirstName()))
            updatedFields.put("firstName", updatedUser.getFirstName());
        if (!Objects.equals(existingUser.getLastName(), updatedUser.getLastName()))
            updatedFields.put("lastName", updatedUser.getLastName());
        if (!Objects.equals(existingUser.getEmail(), updatedUser.getEmail()))
            updatedFields.put("email", updatedUser.getEmail());
        if (!Objects.equals(existingUser.getPassword(), updatedUser.getPassword()))
            updatedFields.put("password", updatedUser.getPassword());
        if (!Objects.equals(existingUser.getPhone(), updatedUser.getPhone()))
            updatedFields.put("phone", updatedUser.getPhone());
        if (!Objects.equals(existingUser.getRole(), updatedUser.getRole()))
            updatedFields.put("role", updatedUser.getRole());
        if (!Objects.equals(existingUser.getDepartment(), updatedUser.getDepartment()))
            updatedFields.put("department", updatedUser.getDepartment());
        if (existingUser.isForcePasswordChange() != updatedUser.isForcePasswordChange())
            updatedFields.put("forcePasswordChange", String.valueOf(updatedUser.isForcePasswordChange()));
        return updatedFields;
    }

    private String capitalize(String field) {
        return (field == null || field.isEmpty()) ? field : field.substring(0, 1).toUpperCase() + field.substring(1);
    }

    private String buildEmailContent(Map<String, String> updatedFields) {
        StringBuilder sb = new StringBuilder();
        sb.append("The following fields have been updated: <br>\n");
        for (Map.Entry<String, String> entry : updatedFields.entrySet()) {
            sb.append(capitalize(entry.getKey())).append(": ").append(entry.getValue()).append("<br>\n");
        }
        return sb.toString();
    }

    public String updateUser(Long id, User user) throws IOException {
        Optional<User> optionalExistingUser = userRepository.findById(id);
        if (optionalExistingUser.isEmpty()) {
            log.warn("User with ID {} not found.", id);
            return "User not found!";
        }

        User existingUser = optionalExistingUser.get();
        Map<String, String> updatedFields = compareAndGetUpdatedFields(existingUser, user);

        // Update only changed fields
        if (updatedFields.containsKey("firstName")) existingUser.setFirstName(user.getFirstName());
        if (updatedFields.containsKey("lastName")) existingUser.setLastName(user.getLastName());
        if (updatedFields.containsKey("email")) existingUser.setEmail(user.getEmail());
        if (updatedFields.containsKey("password")) existingUser.setPassword(user.getPassword());
        if (updatedFields.containsKey("phone")) existingUser.setPhone(user.getPhone());
        if (updatedFields.containsKey("role")) existingUser.setRole(user.getRole());
        if (updatedFields.containsKey("department")) existingUser.setDepartment(user.getDepartment());
        if (updatedFields.containsKey("forcePasswordChange")) existingUser.setForcePasswordChange(user.isForcePasswordChange());

        try {
            userRepository.save(existingUser);
            log.info("User with ID {} updated successfully.", id);

            if (!updatedFields.isEmpty()) {
                String emailContent = buildEmailContent(updatedFields);
                ClassPathResource resource = new ClassPathResource("static/updateUserRegistration.html");
                String htmlTemplate = new String(Files.readAllBytes(resource.getFile().toPath()), StandardCharsets.UTF_8);
                htmlTemplate = htmlTemplate.replace("${firstName}", user.getFirstName())
                                           .replace("${emailContent}", emailContent);

                emailService.sendEmail(user.getEmail(), "Registration Updation", htmlTemplate);
            }

            return "User updated successfully!";
        } catch (DataAccessException ex) {
            log.error("Error occurred while updating user", ex);
            throw new CustomDatabaseException("Unable to update the User");
        }
    }

    public Map<String, String> verify(User user) {
        log.info("Verifying login for email: {}", user.getEmail());
        Map<String, String> res = new HashMap<>();
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(user.getEmail(), user.getPassword()));
            User verifiedUser = userRepository.findByEmail(user.getEmail());
            if (authentication.isAuthenticated()) {
                String token = jwtService.generateToken(user.getEmail(), verifiedUser.getRole());
                res.put("token", token);
                res.put("username", verifiedUser.getEmail());
                res.put("role", verifiedUser.getRole());
                log.info("User {} authenticated successfully.", user.getEmail());
                return res;
            }
        } catch (BadCredentialsException e) {
            log.warn("Invalid login attempt for user: {}", user.getEmail());
            res.put("error", "Invalid username or password");
        } catch (Exception e) {
            log.error("Authentication failed for user {}: {}", user.getEmail(), e.getMessage());
            res.put("error", "Authentication failed due to an internal error");
        }
        return res;
    }
    public boolean sendOTP(String email) {
		log.info("Sending OTP to email: {}", email);
		try {
			User user = userRepo.findByEmail(email);
			if (user == null) {
				log.warn("OTP request failed. No user found with email: {}", email);
				return false;
			}
 
			String otp = generateOTP();
 
			MimeMessage mimeMessage = javaMailSender.createMimeMessage();
 
			MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);
 
			helper.setFrom(new InternetAddress(FROM_EMAIL));
 
			helper.setTo(email);
 
			helper.setSubject("Your One-Time Password (OTP)");
 
			String emailBody = "Dear User,<br><br>"
 
					+ "Your One-Time Password (OTP) is <b>" + otp + "</b>.<br><br>"
 
					+ "Please use this OTP to complete your verification. "
 
					+ "For security reasons, this code will expire in <b>5 minutes</b>.<br><br>"
 
					+ "If you did not request this OTP, please ignore this message.<br><br>"
 
					+ "Thank you.";
 
			helper.setText(emailBody, true);
 
			javaMailSender.send(mimeMessage);
 
			otpStore.put(email, otp);
 
			otpTimestamps.put(email, System.currentTimeMillis());
			log.info("OTP sent successfully to: {}", email);
			return true;
 
		} catch (MessagingException e) {
			log.error("Failed to send OTP to email {}: {}", email, e.getMessage(), e);
			return false;
 
		}
 
	}
 
	private String generateOTP() {
		Random random = new Random();
		int otp = 100000 + random.nextInt(900000);
		return String.valueOf(otp);
	}
 
	public boolean verifyOTP(String email, String otp) {
		log.info("Verifying OTP for email: {}", email);
		String storedOTP = otpStore.get(email);
 
		Long timestamp = otpTimestamps.get(email);
 
		if (storedOTP == null || timestamp == null) {
			log.warn("OTP verification failed. No stored OTP or timestamp for email: {}", email);
			return false;
		}
 
		long currentTime = System.currentTimeMillis();
		if (currentTime - timestamp > 5 * 60 * 1000) {
			log.warn("OTP for email {} has expired.", email);
			otpStore.remove(email);
			otpTimestamps.remove(email);
			return false;
		}
		return storedOTP.equals(otp);
 
	}
 
	public boolean resetPassword(String email, String hashedPassword) {
		log.info("Resetting password for email: {}", email);
		String normalizedEmail = email.trim().toLowerCase();
 
		User user = userRepo.findByEmail(normalizedEmail);
 
		if (user == null) {
			log.warn("Password reset failed. User not found with email: {}", email);
			return false;
 
		}
 
		user.setPassword(hashedPassword);
 
		userRepo.save(user);
		log.info("Password reset successful for email: {}", email);
		return true;
 
	}
	
	public Map<String, String> changePassword(String oldPassword, String newPassword, String confirmPassword, String token) {
        Map<String, String> response = new HashMap<>();
        String username = jwtFilter.extractUsername(token);
        User user = userRepository.findByEmail(username);
 
        if (user == null) {
            response.put("error", "User not found");
            return response;
        }
 
        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            response.put("error", "Old password does not match");
            return response;
        }
 
        if (!newPassword.equals(confirmPassword)) {
            response.put("error", "New password and confirm password do not match");
            return response;
        }
 
        user.setPassword(passwordEncoder.encode(newPassword));
        user.setForcePasswordChange(true);
        userRepository.save(user);
 
        response.put("message", "Password changed successfully. You can now log in with your new password.");
        return response;
    }
 
}
