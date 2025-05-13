package com.aja.ott.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.aja.ott.entity.PaymentHistory;

@Repository
	public interface PaymentHistoryRepository extends JpaRepository<PaymentHistory, Long> {
	}
