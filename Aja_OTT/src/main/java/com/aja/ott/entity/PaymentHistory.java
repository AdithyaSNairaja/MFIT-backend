package com.aja.ott.entity;

import java.math.BigDecimal;
import java.sql.Date;
import java.time.LocalDate;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentHistory {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private LocalDate date;

	private BigDecimal transactionAmount;
	private String transactionId;
	private BigDecimal remainingAmount;
	private String name;
	private String paymentMode;
	private String transactionStatus;
	private String gatewayResponseCode;
	private String gatewayName;
	private String transactionType;
	private BigDecimal refundAmount;

	private LocalDate refundedAt;

//	@ManyToOne
//	@JoinColumn(name = "trial_user_id")
//	private TrialUser trialUser;
}
