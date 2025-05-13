package com.aja.ott.service;


import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import java.util.Optional;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import jakarta.mail.MessagingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import com.aja.ott.entity.OrientationUser;
import com.aja.ott.exception.CustomDatabaseException;
import com.aja.ott.exception.ScoreException;
import com.aja.ott.exception.UserAlreadyExistsException;
import com.aja.ott.repository.OrientationUserRepository;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class OrientationUserService {

	@Autowired
	private OrientationUserRepository orientationUserRepository;

	@Autowired
	private EmailService emailService;

	// Validation method to ensure score is between 1 and 15 (inclusive)
	private int validateScore(int score) {
		if (score < 0 || score > 15) {
			throw new ScoreException("Score must be between 1 and 15. Provided score: " + score);
		}
		return score;
	}

	public String saveUser(OrientationUser orientationUser) {
		
		if (orientationUserRepository.findByEmail(orientationUser.getEmail()) != null) {
			log.warn("User already exists with email: {}", orientationUser.getEmail());
			throw new UserAlreadyExistsException("User has already been registered with this email.");
		}
		if (orientationUserRepository.existsByContactNo(orientationUser.getContactNo())) {
			log.warn("User already exists with phone: {}", orientationUser.getContactNo());
			throw new UserAlreadyExistsException("User has already been registered with this phone number.");
		}
		log.info("Saving new orientation user: {}", orientationUser);

		// Validate scores before saving
		orientationUser.setTechnical(validateScore(orientationUser.getTechnical()));
		orientationUser.setAptitude(validateScore(orientationUser.getAptitude()));
		orientationUser.setCommunication(validateScore(orientationUser.getCommunication()));

		// Calculate total score
		int total = orientationUser.getAptitude() + orientationUser.getCommunication() + orientationUser.getTechnical();
		orientationUser.setTotalScore(total);

		try {
			log.info("Saving new orientation user: {}", orientationUser);
			orientationUserRepository.save(orientationUser);
			log.info("User saved successfully.");
			return "User appointment saved successfully!";
			}
			catch (DataAccessException ex) {
			    log.error("Database error occurred", ex);
			    throw new CustomDatabaseException("Unable to save", ex);
			}

	}

	public Page<OrientationUser> getUser(int page,int size) {
		try {
			log.info("Fetching all non-deleted orientation users.");
			PageRequest pageRequest = PageRequest.of(page, size);
			return orientationUserRepository.findByIsDeletedFalse(pageRequest);
			}
			catch (DataAccessException ex) {
			    log.error("Database error occurred", ex);
			    throw new CustomDatabaseException("Unable to fetch the data ");
			}

	}

	public Optional<OrientationUser> detailsBasedOnId(Long id) {
		log.info("Fetching orientation user by ID: {}", id);
		Optional<OrientationUser> user = orientationUserRepository.findById(id);
		log.info("User fetched for ID {}: {}", id, user);
		return user;
	}

	public boolean softDeleteUser(Long id) {
		log.info("Attempting soft delete for user ID: {}", id);
		Optional<OrientationUser> userOptional = orientationUserRepository.findById(id);
		log.debug("User optional result: {}", userOptional);
		if (userOptional.isPresent()) {
			OrientationUser user = userOptional.get();
			log.info("User found: {}", user);
			if (user.isDeleted()) {
				log.warn("User already soft deleted: {}", id);
				return false;
			}
			user.setDeleted(true);
			try {
				orientationUserRepository.save(user);
				log.info("User soft deleted successfully: {}", id);
				return true;
				}
				catch (DataAccessException ex) {
				    log.error("Database error occurred", ex);
				    throw new CustomDatabaseException("Unable to delete ");
				}

		}
		log.warn("User not found for ID: {}", id);
		return false;
	}

	public OrientationUser updateUser(Long id, OrientationUser orientationUser) {
		log.info("Updating user with ID: {}", id);
		Optional<OrientationUser> existingUserOpt = orientationUserRepository.findById(id);
		log.debug("Existing user optional for ID {}: {}", id, existingUserOpt);
		if (existingUserOpt.isPresent()) {
			OrientationUser user = existingUserOpt.get();
			log.info("Existing user found: {}", user);

			if (orientationUser.getName() != null)
				user.setName(orientationUser.getName());
			if (orientationUser.getContactNo() != "0")
				user.setContactNo(orientationUser.getContactNo());
			if (orientationUser.getTechnology() != null)
				user.setTechnology(orientationUser.getTechnology());
			if (orientationUser.getEmail() != null)
				user.setEmail(orientationUser.getEmail());
			if (orientationUser.getOrientationDate() != null)
				user.setOrientationDate(orientationUser.getOrientationDate());
			if (orientationUser.getOrientationGivenBy() != null)
				user.setOrientationGivenBy(orientationUser.getOrientationGivenBy());
			if (orientationUser.getParentGuardianName() != null)
				user.setParentGuardianName(orientationUser.getParentGuardianName());
			if (orientationUser.getParentGuardianRelation() != null)
				user.setParentGuardianRelation(orientationUser.getParentGuardianRelation());
			if (orientationUser.getParentGuardianContact() != null)
				user.setParentGuardianContact(orientationUser.getParentGuardianContact());
			if (orientationUser.getReferralSource() != null)
				user.setReferralSource(orientationUser.getReferralSource());
			if (orientationUser.getPreviousCompanyName() != null)
				user.setPreviousCompanyName(orientationUser.getPreviousCompanyName());
			if (orientationUser.getEmploymentStartDate() != null)
				user.setEmploymentStartDate(orientationUser.getEmploymentStartDate());
			if (orientationUser.getEmploymentEndDate() != null)
				user.setEmploymentEndDate(orientationUser.getEmploymentEndDate());
			if (orientationUser.getIndustry() != null)
				user.setIndustry(orientationUser.getIndustry());
			if (orientationUser.getInstituteName() != null)
				user.setInstituteName(orientationUser.getInstituteName());
			if (orientationUser.getDegreeOrDiploma() != null)
				user.setDegreeOrDiploma(orientationUser.getDegreeOrDiploma());
			if (orientationUser.getSpecialization() != null)
				user.setSpecialization(orientationUser.getSpecialization());
			if (orientationUser.getDateOfCompletion() != null)
				user.setDateOfCompletion(orientationUser.getDateOfCompletion());
			if (orientationUser.getPaymentMode() != null)
				user.setPaymentMode(orientationUser.getPaymentMode());
			if (orientationUser.getDate() != null)
				user.setDate(orientationUser.getDate());
			if (orientationUser.getSignature() != null)
				user.setSignature(orientationUser.getSignature());

			user.setHasTechnologyTraining(orientationUser.isHasTechnologyTraining());
			user.setInterestedToJoin(orientationUser.isInterestedToJoin());
			user.setTrialPeriodInterest(orientationUser.isTrialPeriodInterest());
			user.setDeleted(orientationUser.isDeleted());

			// Validate and set scores before updating
			user.setTechnical(validateScore(orientationUser.getTechnical()));
			user.setAptitude(validateScore(orientationUser.getAptitude()));
			user.setCommunication(validateScore(orientationUser.getCommunication()));

			// Calculate total score after validation
			int total = user.getAptitude() + user.getCommunication() + user.getTechnical();
			user.setTotalScore(total);

			try{
				OrientationUser savedUser = orientationUserRepository.save(user);
				log.info("User updated successfully: {}", savedUser);
				return savedUser;
				}
				catch (DataAccessException ex) {
				    log.error("Database error occurred", ex);
				    throw new CustomDatabaseException("Unable to update");
				}

		} else {
			log.error("User with ID {} not found. Cannot update.", id);
			throw new RuntimeException("User with ID " + id + " not found");
		}
	}
	public void checkFollowUpUsers() throws MessagingException, IOException {
		log.info("Checking users whose follow-up is scheduled for tomorrow...");

		LocalDate tomorrow = LocalDate.now().plusDays(1);
		List<OrientationUser> followUpUsers = orientationUserRepository.findByFollowUpDate(tomorrow);

		if (!followUpUsers.isEmpty()) {
			log.info("Found {} users for follow-up on {}", followUpUsers.size(), tomorrow);

			ClassPathResource hrTemplate = new ClassPathResource("static/HREmail.html");
			String templateContent = new String(Files.readAllBytes(hrTemplate.getFile().toPath()), StandardCharsets.UTF_8);

			if (templateContent == null || templateContent.trim().isEmpty()) {
				log.error("HREmail.html template is empty or not found");
				return;
			}

			String hrEmail = extractHrEmail(templateContent);
			if (hrEmail == null) {
				log.error("HR email not found in HREmail.html");
				return;
			}

			StringBuilder emailBody = new StringBuilder();

			for (OrientationUser user : followUpUsers) {
				String filledTemplate = templateContent
						.replace("${candidateName}", Optional.ofNullable(user.getName()).orElse("N/A"))
						.replace("${date}", Optional.ofNullable(user.getFollowUpDate()).map(LocalDate::toString).orElse("N/A"))
						.replace("${technology}", Optional.ofNullable(user.getTechnology()).orElse("N/A"))
						.replace("${time}", Optional.ofNullable(user.getOrientationTime()).map(LocalTime::toString).orElse("N/A"));

				emailBody.append(filledTemplate).append("<hr/>");
			}

			emailService.sendEmail(
					hrEmail,
					"Reminder: Follow-Up Scheduled for Tomorrow",
					emailBody.toString()
			);

			log.info("Follow-up reminder sent to HR.");
		} else {
			log.info("No follow-up scheduled for {}", tomorrow);
		}
	}

	private String extractHrEmail(String templateContent) {
		Pattern pattern = Pattern.compile("<!--\\s*hr-email:\\s*([\\w._%+-]+@[\\w.-]+\\.[A-Za-z]{2,})\\s*-->");
		Matcher matcher = pattern.matcher(templateContent);
		return matcher.find() ? matcher.group(1) : null;
	}

}
