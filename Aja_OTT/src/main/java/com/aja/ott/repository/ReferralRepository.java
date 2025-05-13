package com.aja.ott.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.aja.ott.entity.Referral;

@Repository
public interface ReferralRepository extends JpaRepository<Referral, Long> {
	    Optional<Referral> findByName(String name);
	
}
