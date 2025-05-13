package com.aja.ott.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.aja.ott.entity.TrialUserFeedBack;
import com.aja.ott.service.TrialUserFeedBackService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
public class TrialUserFeedBackController {

	@Autowired
	private final TrialUserFeedBackService trialUserFeedBackService;

	public TrialUserFeedBackController(TrialUserFeedBackService trialUserFeedBackService) {
		this.trialUserFeedBackService = trialUserFeedBackService;
	}

	@PostMapping("/save-trial-user-feedback")
	public ResponseEntity<String> saveTrialUserFeedback(@RequestBody TrialUserFeedBack trialUserFeedBack) {
		try{log.info("Received request to save feedback: {}", trialUserFeedBack);
		String response = trialUserFeedBackService.createFeedback(trialUserFeedBack);
		log.info("Feedback saved successfully: {}", response);
		return ResponseEntity.ok(response);
		}
		catch (Exception e) {
			log.error("Error registering user: {}", e);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body("Error registering user: " + e.getMessage());
		}
	}

	@PutMapping("/update-trial-user-feedback/{id}")
	public ResponseEntity<TrialUserFeedBack> updateTrialUserFeedback(@PathVariable Long id,
			@RequestBody TrialUserFeedBack trialUserFeedBack) {
		TrialUserFeedBack updated = trialUserFeedBackService.updateFeedback(id, trialUserFeedBack);
		return ResponseEntity.ok(updated);
	}

	@GetMapping("/get-all-trial-user-feedbacks")
	public List<TrialUserFeedBack> getAllTrialUserFeedbacks() {
		log.info("Fetching all trial user feedbacks");
		return trialUserFeedBackService.getAll();
	}
	@GetMapping("/get-trail-exit")
	public String getAll() {
		log.info("Fetching all trial user feedbacks no (getFeed endpoint)");
		return trialUserFeedBackService.getcount();
	}


	@GetMapping("/get-trial-user-feedback-by-id/{id}")
	public ResponseEntity<Optional<TrialUserFeedBack>> getTrialUserFeedbackById(@PathVariable Long id) {
		log.info("Fetching feedback by ID: {}", id);
		Optional<TrialUserFeedBack> feedback = trialUserFeedBackService.getFeedbackById(id);
		return feedback.isPresent() ? ResponseEntity.ok(feedback) : ResponseEntity.notFound().build();
	}

	@DeleteMapping("/delete-trial-user-feedback/{id}")
	public ResponseEntity<String> deleteTrialUserFeedback(@PathVariable Long id) {
		log.info("Attempting to soft delete feedback with ID: {}", id);
		boolean deleted = trialUserFeedBackService.softDeleteUser(id);
		if (deleted) {
			log.info("Feedback soft deleted successfully for ID: {}", id);
			return ResponseEntity.status(HttpStatus.OK).body("User soft deleted successfully.");
		} else {
			log.warn("Feedback not found or already deleted for ID: {}", id);
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found or already deleted.");
		}
	}

}
