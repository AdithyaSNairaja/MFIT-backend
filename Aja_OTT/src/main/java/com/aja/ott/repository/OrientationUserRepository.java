package com.aja.ott.repository;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.aja.ott.entity.OrientationUser;

@Repository
public interface OrientationUserRepository extends JpaRepository<OrientationUser, Long> {
	Page<OrientationUser> findByIsDeletedFalse(Pageable pageable);
	//  User findByEmail(String email);
	OrientationUser findByEmail(String email);
	boolean existsByContactNo(String contactNo);
	List<OrientationUser> findByFollowUpDate(LocalDate followUpdate);

}
