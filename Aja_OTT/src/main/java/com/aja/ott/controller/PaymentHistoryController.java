package com.aja.ott.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.aja.ott.entity.PaymentHistory;
import com.aja.ott.service.PaymentHistoryService;


@RestController
public class PaymentHistoryController {
@Autowired
 private final PaymentHistoryService paymentHistoryService;

public PaymentHistoryController(PaymentHistoryService paymentHistoryService) {
	this.paymentHistoryService = paymentHistoryService;
			
}
@PostMapping("/save-payment-history")
public ResponseEntity<String> saveOrientationUser(@RequestBody PaymentHistory  paymentHistory) {
    String response = paymentHistoryService.savePaymentHistory(paymentHistory);
    return ResponseEntity.ok(response);
}


	
}
