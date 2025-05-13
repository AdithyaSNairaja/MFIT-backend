package com.aja.ott.entity;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data // Generates Getters, Setters, equals(), hashCode(), and toString()
@NoArgsConstructor // Generates a No-Arg Constructor
@AllArgsConstructor // Generates an All-Arg Constructor

@Entity
public class TrialUser {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String name;
	private String contactNo;
	private String technology;
	private String email;

	private LocalDate orientationDate;
	private String orientationGivenBy;
	private boolean hasTechnologyTraining;
	private String parentGuardianName;
	private String parentGuardianRelation;
	private String parentGuardianContact;
	private String referralSource;

	// Previous Employment
	private String previousCompanyName;
	private LocalDate employmentStartDate;
	private LocalDate employmentEndDate;
	private String industry;

	// Education Details
	private String instituteName;
	private String degreeOrDiploma;
	private String specialization;
	private LocalDate dateOfCompletion;

	// HR Comments
	private boolean interestedToJoin;
	private boolean trialPeriodInterest;

	private String paymentMode;
	private LocalDate date;
	private String signature;

	private LocalDate trialStartDate;
	private LocalDate trialEndDate;

	private String firstName;
	private String lastName;
	private LocalDate dateOfBirth;
	private String gender;
	private Integer age;
	private String maritalStatus;
	private String panCardNumber;
	private String aadharCardNumber;
	private String primaryPhoneNumber;
	private String alternativePhoneNumber;
	private String emergencyPhoneNumber1;
	private String emergencyPhoneNumber2;
	private String presentAddress;
	private String permanentAddress;
	private Boolean acknowledgeTermsAndConditions;

	private String comment;
	@Enumerated(EnumType.STRING)
	private TrialExtendStatus trialExtendStatus = TrialExtendStatus.NO;

	@Column(nullable = false)
	private boolean isDeleted = false;

	
//	@OneToMany(mappedBy = "trialUser", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
//	private List<PaymentHistory> paymentHistory  = new ArrayList<>();;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private Status status = Status.PENDING;

	public enum Status {
		PENDING, ACTIVE, CONVERTED, QUIT
	}


}
