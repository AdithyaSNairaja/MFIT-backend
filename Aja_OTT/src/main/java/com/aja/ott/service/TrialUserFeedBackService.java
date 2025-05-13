package com.aja.ott.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import com.aja.ott.entity.TrialUser;
import com.aja.ott.entity.TrialUserFeedBack;
import com.aja.ott.exception.CustomDatabaseException;
import com.aja.ott.exception.UserAlreadyExistsException;
import com.aja.ott.repository.TrialUserFeedBackRepository;
import com.aja.ott.repository.TrialUserRepository;

import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class TrialUserFeedBackService {

	@Autowired
	private TrialUserFeedBackRepository trialUserFeedBackRepository;
	@Autowired
	private TrialUserRepository trialUserRepository;

	public String createFeedback(TrialUserFeedBack trialUserFeedBack) {
		if (trialUserFeedBackRepository.findByEmail(trialUserFeedBack.getEmail()) != null) {
			throw new UserAlreadyExistsException("User already exists with this email.");
		}

		if (trialUserFeedBackRepository.existsByContactNo(trialUserFeedBack.getContactNo())) {
			throw new UserAlreadyExistsException("User already exists with this phone number.");
		}

		try {
			TrialUserFeedBack savedFeedback = trialUserFeedBackRepository.save(trialUserFeedBack);

			TrialUser trialUser = trialUserFeedBack.getTrialUser();
			if (trialUser != null && TrialUser.Status.PENDING.equals(trialUser.getStatus())) {
				trialUser.setStatus(TrialUser.Status.QUIT);
				trialUserRepository.save(trialUser);
			}

			return "Feedback saved and TrialUser status updated";
		} catch (DataAccessException ex) {
			throw new CustomDatabaseException("Unable to save feedback.");
		}
	}

	public List<TrialUserFeedBack> getAll() {
		try {
			log.info("Fetching all feedbacks with isDeleted = false");
			return trialUserFeedBackRepository.findByIsDeletedFalse();
		} catch (DataAccessException ex) {
			log.error("Database error occurred", ex);
			throw new CustomDatabaseException("Unable to get the Feedback");
		}

	}

	public Optional<TrialUserFeedBack> getFeedbackById(Long id) {
		log.info("Fetching feedback by ID: {}", id);
		return trialUserFeedBackRepository.findByIdAndIsDeletedFalse(id);
	}

	public boolean softDeleteUser(Long id) {
		log.info("Attempting to soft delete feedback with ID: {}", id);
		Optional<TrialUserFeedBack> userOptional = trialUserFeedBackRepository.findById(id);

		if (userOptional.isPresent()) {
			TrialUserFeedBack feedBack = userOptional.get();
			if (feedBack.isDeleted()) {
				log.warn("Feedback already deleted with ID: {}", id);
				return false;
			}
			feedBack.setDeleted(true);
			try {
				trialUserFeedBackRepository.save(feedBack);
				log.info("Feedback soft deleted successfully with ID: {}", id);
				return true;
			} catch (DataAccessException ex) {
				log.error("Database error occurred", ex);
				throw new CustomDatabaseException("Unable to delete the feedback");
			}

		}
		log.warn("Feedback not found with ID: {}", id);
		return false;
	}

	public String getcount() {
		log.info("Fetching all feedbacks with isDeleted = false");
		List<TrialUserFeedBack> trialUserFeedBack = trialUserFeedBackRepository.findByIsDeletedFalse();
		return "The number of Trial exit is " + trialUserFeedBack.size();
	}

	public TrialUserFeedBack updateFeedback(Long id, TrialUserFeedBack trialUserFeedBack) {
		log.info("Attempting to update feedback with ID: {}", id);
		Optional<TrialUserFeedBack> existingOptional = trialUserFeedBackRepository.findByIdAndIsDeletedFalse(id);

		if (existingOptional.isEmpty()) {
			log.warn("Feedback not found or already deleted for ID: {}", id);
			throw new RuntimeException("Feedback not found or deleted with ID: " + id);
		}

		TrialUserFeedBack existing = existingOptional.get();

		existing.setName(trialUserFeedBack.getName());
		existing.setEmail(trialUserFeedBack.getEmail());
		existing.setContactNo(trialUserFeedBack.getContactNo());
		existing.setTechnology(trialUserFeedBack.getTechnology());
		existing.setOverallExperience(trialUserFeedBack.getOverallExperience());
		existing.setContentQuality(trialUserFeedBack.getContentQuality());
		existing.setCodingTutorSupport(trialUserFeedBack.getCodingTutorSupport());
		existing.setEngagementAndInteractivity(trialUserFeedBack.getEngagementAndInteractivity());
		existing.setHowMuchConfidenceYouGained(trialUserFeedBack.getHowMuchConfidenceYouGained());
		existing.setAnySpecificExperienceOrAspectsOfProgram(
				trialUserFeedBack.getAnySpecificExperienceOrAspectsOfProgram());
		existing.setPeerInteraction(trialUserFeedBack.getPeerInteraction());
		existing.setLearningExperienceMeetExpectations(trialUserFeedBack.getLearningExperienceMeetExpectations());
		existing.setSpecificAspectsoflearning(trialUserFeedBack.getSpecificAspectsoflearning());
		existing.setSpecificAspectsoflearningReachYourExpectation(
				trialUserFeedBack.getSpecificAspectsoflearningReachYourExpectation());
		existing.setConcernsIssueWithAja(trialUserFeedBack.getConcernsIssueWithAja());
		existing.setDecisionOfJoiningAja(trialUserFeedBack.getDecisionOfJoiningAja());
		existing.setAdditionalComment(trialUserFeedBack.getAdditionalComment());

		try {
			TrialUserFeedBack saved = trialUserFeedBackRepository.save(existing);
			log.info("Feedback updated successfully: {}", saved);
			return saved;
		} catch (DataAccessException ex) {
			log.error("Database error occurred", ex);
			throw new CustomDatabaseException("Unable to update the feedback");
		}
	}
}
