package com.aja.ott.entity;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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
public class OrientationUser {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
//	 One-to-Many Relationship with UserAppointment
//	@OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
//	private List<OrientationUserAppointment> appointments;

	private String name;
	private String contactNo;
	private String technology;
	private String email;
	private LocalDate orientationDate;
	private String orientationGivenBy;
	private LocalTime orientationTime;
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
	
	//Online Test
	private int technical;
	private int aptitude;
	private int communication;
	private int totalScore;

	// HR Comments
	private boolean interestedToJoin;
	private boolean trialPeriodInterest;
	private String paymentMode;
	private LocalDate date;
	private String signature;
	
	@Column(nullable = false)
	private boolean isDeleted = false;

	private LocalDate followUpDate;
}
