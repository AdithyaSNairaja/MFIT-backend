package com.aja.ott.entity;

import java.time.LocalDate;
import java.time.LocalTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrientationUserAppointment {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	// Uncomment and map if needed for relational data
	// @ManyToOne
	// @JoinColumn(name = "user_id", nullable = false)
	// private OrientationUser user;

	private String candidateName;
	private String email;
	private String phone;
	private LocalDate date;
	private LocalTime time;
	private String address;
	private String status;

	@Column(nullable = false)
	private boolean isDeleted = false;
}
