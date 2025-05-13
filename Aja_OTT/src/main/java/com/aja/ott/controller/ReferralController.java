package com.aja.ott.controller;

import java.util.Optional;
import org.springframework.web.bind.annotation.RequestBody;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.aja.ott.entity.Referral;
import com.aja.ott.service.ReferralService;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
public class ReferralController {
	@Autowired
	private ReferralService referralService;

	@PostMapping("/save-referral")
	public ResponseEntity<Referral> saveReferral(@RequestBody Referral referral) {
		log.info("Received referral to save: {}", referral);
		Referral savedReferral = referralService.addReferral(referral);
		return new ResponseEntity<>(savedReferral, HttpStatus.CREATED);
	}

	@GetMapping("/get-by-name")
	public ResponseEntity<Referral> getReferralByName(@RequestParam String name) {
		log.info("Fetching referral by name: {}", name);
		Optional<Referral> referral = referralService.getReferralByName(name);

		return referral.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
	}

}
