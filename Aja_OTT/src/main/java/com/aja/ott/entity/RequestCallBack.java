package com.aja.ott.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;

@Entity
@Data
public class RequestCallBack {
	@Id
	@GeneratedValue (strategy =GenerationType.IDENTITY)
	private Long id;
	private String name;
	private String mobileNumber;
	

	@Column(nullable = false)
	private boolean isDeleted = false;

}
