package com.aja.ott.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.aja.ott.entity.Referral;
import com.aja.ott.repository.ReferralRepository;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class ReferralService {
	@Autowired
	private ReferralRepository referralRepository;

	public Referral addReferral(Referral referral) {
		log.info("Saving referral: {}", referral);
		return referralRepository.save(referral);
	}

	public Optional<Referral> getReferralByName(String name) {
		return referralRepository.findByName(name);
	}

}
