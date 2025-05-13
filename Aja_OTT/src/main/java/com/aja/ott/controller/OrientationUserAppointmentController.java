package com.aja.ott.controller;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
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

import com.aja.ott.entity.OrientationUserAppointment;
import com.aja.ott.service.OrientationUserAppointmentService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
public class OrientationUserAppointmentController {

	@Autowired
	private final OrientationUserAppointmentService orientationUserAppointmentService;

	public OrientationUserAppointmentController(OrientationUserAppointmentService orientationUserAppointmentService) {

		this.orientationUserAppointmentService = orientationUserAppointmentService;
	}

	@PostMapping("/save-orientation-user-appointment")
	ResponseEntity<String> saveOrientationUserAppointment(
			@RequestBody OrientationUserAppointment orientationUserAppointment) {
		log.info("Received request to save user appointment: {}", orientationUserAppointment);
		try {
			orientationUserAppointmentService.saveUserAppointment(orientationUserAppointment);
			log.info("User appointment saved successfully.");
		} catch (IOException e) {
			log.error("Error saving user appointment", e);
		}
		return ResponseEntity.ok("User registered and email sent.");
	}

	@PutMapping("/update-orientation-user-appointment/{Id}")
	ResponseEntity<String> updateOrientationUserAppointment(@PathVariable Long Id,
			@RequestBody OrientationUserAppointment orientationUserAppointment) {
		log.info("Received request to update user appointment with ID: {}", Id);

		try {
			orientationUserAppointmentService.updateUserAppointment(Id, orientationUserAppointment);
		} catch (IOException e) {
			log.error("Error updating user appointment", e);
		}
		log.info("Appointment has been updated");
		return ResponseEntity.ok("appointment updated and email sent.");
	}

	@GetMapping("/get-all-orientation-user-appointment")
	public List<OrientationUserAppointment> getAllOrientationUserAppointment() {
		log.info("Fetching all orientation user appointments.");
		return orientationUserAppointmentService.getAll();
	}

	@GetMapping("/get-orientation-user-appointment-by-id/{id}")
	public ResponseEntity<OrientationUserAppointment> getOrientationUserAppointmentById(@PathVariable Long id) {
		log.info("Fetching orientation user appointment with ID: {}", id);
		Optional<OrientationUserAppointment> orientationUserAppointment = orientationUserAppointmentService
				.detailsBasedOnId(id);
		return orientationUserAppointment.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());

	}

	@DeleteMapping("/delete-orientation-user-appointment/{id}")
	public ResponseEntity<String> deleteOrientationUserAppointment(@PathVariable Long id) {
		log.info("Received request to soft delete user appointment with ID: {}", id);
		boolean deleted = orientationUserAppointmentService.softDeleteUser(id);
		if (deleted) {
			log.info("User soft deleted successfully for ID: {}", id);
			return ResponseEntity.status(HttpStatus.OK).body("User soft deleted successfully.");
		} else {
			log.warn("User not found or already deleted for ID: {}", id);
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found or already deleted.");
		}
	}

	@PostMapping("/cancel-orientation-user-appointment/{id}")
	public ResponseEntity<String> cancelOrientationUserAppointment(@PathVariable Long id) {
		log.info("Received request to cancel user appointment with ID: {}", id);
		try {
			boolean cancelled = orientationUserAppointmentService.cancelUserAppointment(id);
			if (cancelled) {
				log.info("Appointment with ID: {} has been cancelled.", id);
				return ResponseEntity.ok("Appointment cancelled successfully.");
			} else {
				log.warn("Appointment with ID: {} not found or already cancelled.", id);
				return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Appointment not found or already cancelled.");
			}
		} catch (Exception e) {
			log.error("Error cancelling appointment with ID: {}", id, e);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error cancelling appointment.");
		}
	}

	@GetMapping("/upcoming-appointment")
	public List<OrientationUserAppointment> getUpcomingAppointments(
			@RequestParam("start") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
			@RequestParam("end") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
		return orientationUserAppointmentService.getUpcomingAppointments(startDate, endDate);
	}

}
