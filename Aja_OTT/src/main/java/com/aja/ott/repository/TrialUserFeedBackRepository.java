package com.aja.ott.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.aja.ott.entity.TrialUser;
import com.aja.ott.entity.TrialUserFeedBack;

@Repository
public interface TrialUserFeedBackRepository extends JpaRepository<TrialUserFeedBack, Long> {

	public List<TrialUserFeedBack> findByIsDeletedFalse();

	public Optional<TrialUserFeedBack> findByIdAndIsDeletedFalse(Long id);
	
	TrialUserFeedBack findByEmail(String email);
	boolean existsByContactNo(String contactNo);

}
