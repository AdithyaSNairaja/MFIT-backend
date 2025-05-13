package com.aja.ott.controller;


import java.io.IOException;
import java.util.List;

import java.util.Optional;

import jakarta.mail.MessagingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.aja.ott.entity.OrientationUser;
import com.aja.ott.service.OrientationUserService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
public class OrientationUserController {

	@Autowired
	private final OrientationUserService orientationUserService;

	public OrientationUserController(OrientationUserService orientationUserService) {
		this.orientationUserService = orientationUserService;
	}

	@PostMapping("/save-orientation-user")
	public ResponseEntity<String> saveOrientationUser(@RequestBody OrientationUser orientationUser) {
		try {
		log.info("Received request to save orientation user: {}", orientationUser);
		String response = orientationUserService.saveUser(orientationUser);
		log.info("Save response: {}", response);
		return ResponseEntity.ok(response);
		}
		catch (Exception e) {
			log.error("Error registering user: {}", e);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body("Error registering user: " + e.getMessage());
		}
		
	}

	@GetMapping("/get-all-orientation-users")
	public Page<OrientationUser> getAllOrientationUsers(@RequestParam(defaultValue = "0") int page,
	        @RequestParam(defaultValue = "10") int size) {
		log.info("Fetching all orientation users.");
		return orientationUserService.getUser(page,size);
	}

	@GetMapping("/get-orientation-user-by-id/{id}")
	public ResponseEntity<OrientationUser> getOrientationUserById(@PathVariable Long id) {
		log.info("Fetching orientation user with ID: {}", id);
		Optional<OrientationUser> orientationUser = orientationUserService.detailsBasedOnId(id);
		return orientationUser.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
	}

	@DeleteMapping("/delete-orientation-user/{id}")
	public ResponseEntity<String> deleteOrientationUser(@PathVariable Long id) {
		log.info("Received request to soft delete orientation user with ID: {}", id);
		boolean deleted = orientationUserService.softDeleteUser(id);
		if (deleted) {
			log.info("User soft deleted successfully for ID: {}", id);
			return ResponseEntity.status(HttpStatus.OK).body("User soft deleted successfully.");
		} else {
			log.warn("User not found or already deleted for ID: {}", id);
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found or already deleted.");
		}
	}

	@PutMapping("/update-orientation-user/{id}")
	public ResponseEntity<OrientationUser> updateOrientationUser(@PathVariable Long id,
			@RequestBody OrientationUser orientationUser) {
		log.info("Received request to update user with ID: {}", id);
		OrientationUser user = orientationUserService.updateUser(id, orientationUser);
		log.info("Update operation result for ID {}: {}", id, user);
		return user != null ? ResponseEntity.ok(user) : ResponseEntity.notFound().build();
	}

	@GetMapping("/check-followup")
	public ResponseEntity<String> triggerFollowUpReminder() {
		try {
			orientationUserService.checkFollowUpUsers(); // updated method name here
			return ResponseEntity.ok("✅ Follow-up reminder email sent to HR successfully.");
		} catch (MessagingException | IOException e) {
			e.printStackTrace(); // optional for debugging
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body("❌ Error while sending follow-up reminder: " + e.getMessage());
		}
	}



}
