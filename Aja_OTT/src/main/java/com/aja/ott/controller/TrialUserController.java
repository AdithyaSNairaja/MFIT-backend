package com.aja.ott.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import com.aja.ott.entity.OrientationUser;
import com.aja.ott.entity.TrialUser;
import com.aja.ott.exception.ResourceNotFoundException;
import com.aja.ott.service.TrialUserService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
public class TrialUserController {

	@Autowired
	private final TrialUserService trialUserService;

	public TrialUserController(TrialUserService trialUserService) {
		this.trialUserService = trialUserService;
	}

	@GetMapping("/get-number-of-days-left-for-trial/{id}")
	ResponseEntity<Long> getNumberofdaysLeft(@PathVariable Long id) throws ResourceNotFoundException {
		log.info("Received request to get number of days left for trial user with ID: {}", id);
		long response = trialUserService.getNumberofdaysLeft(id);
		log.info("Number of days left for user ID {}: {}", id, response);
		return ResponseEntity.ok(response);
	}

	@GetMapping("/get-all-trial-users")
	public List<TrialUser> getAllTrialUsers() {
		log.info("Received request to fetch all trial users");
		return trialUserService.getAll();

	}
    
	@GetMapping("/get-trial-users-by-status/{status}")
	public ResponseEntity<List<TrialUser>> getTrialUsersByStatus(@PathVariable TrialUser.Status status) {
		log.info("Fetching trial users with status: {}", status);
		List<TrialUser> users = trialUserService.getTrialUsersByStatus(status);
		return ResponseEntity.ok(users);
	}

	@PostMapping("/save-trial-user")
	public ResponseEntity<String> saveTrialUser(@RequestBody TrialUser trialUser) {
		try {
		log.info("Received request to save a trial user based on orientation user: {}", trialUser);
		String response= trialUserService.saveTrialUser(trialUser);
		log.info("Trial user saved: {}", response);
		return ResponseEntity.ok(response);
		}
		catch (Exception e) {
			log.error("Error registering user: {}", e);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body("Error registering user: " + e.getMessage());
		}
	}
	
	@GetMapping("/get-trial-user-by-id/{id}")
	public ResponseEntity<TrialUser> getTrialUserById(@PathVariable Long id) {
		log.info("Fetching user by ID: {}", id);
		Optional<TrialUser> user = trialUserService.detailsBasedOnId(id);
		return user.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
	}

	@DeleteMapping("/trial-user-delete/{id}")
	public ResponseEntity<String> trialUserDelete(@PathVariable Long id) {
		log.info("Received request to soft delete trial user with ID: {}", id);
		boolean deleted = trialUserService.softDeleteUser(id);
		if (deleted) {
			log.info("Trial user with ID {} soft deleted successfully", id);
			return ResponseEntity.status(HttpStatus.OK).body("User soft deleted successfully.");
		} else {
			log.warn("Trial user with ID {} not found or already deleted", id);
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found or already deleted.");
		}

	}

	@PutMapping("/update-trial-user/{id}")
	public ResponseEntity<TrialUser> updateTrialUser(@PathVariable Long id, @RequestBody TrialUser trialUserDetails) {
		log.info("Received request to update trial user with ID: {} and details: {}", id, trialUserDetails);
		TrialUser updatedUser = trialUserService.updateTrialUser(id, trialUserDetails);
		log.info("Trial user updated: {}", updatedUser);
		return ResponseEntity.ok(updatedUser);
	}
	@PutMapping("/update-trial-user-extend-date/{id}")
	public ResponseEntity<TrialUser> updateTrialUserExtendDate(@PathVariable Long id, @RequestBody TrialUser trialUserDetails) {
		log.info("Received request to update trial user with ID: {} and details: {}", id, trialUserDetails);
		TrialUser updatedUser = trialUserService.updateTrialUserExtend(id, trialUserDetails);
		log.info("Trial user updated: {}", updatedUser);
		return ResponseEntity.ok(updatedUser);
	}

}
