package com.aja.ott.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.aja.ott.entity.PaymentHistory;
import com.aja.ott.repository.PaymentHistoryRepository;

@Service
public class PaymentHistoryService {
	@Autowired
	private final PaymentHistoryRepository paymentHistoryRepository;

	public PaymentHistoryService(PaymentHistoryRepository paymentHistoryRepository) {
		this.paymentHistoryRepository = paymentHistoryRepository;
	}	
	
	public String savePaymentHistory(PaymentHistory paymentHistory ) {
		paymentHistoryRepository.save(paymentHistory);
		return "User appointment saved successfully!";
	}
}
