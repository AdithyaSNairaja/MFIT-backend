package com.aja.ott.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

import com.aja.ott.entity.OrientationUser;
import com.aja.ott.entity.TrialUser;
import com.aja.ott.entity.TrialUser.Status;

import lombok.RequiredArgsConstructor;

@Repository
public interface TrialUserRepository extends JpaRepository<TrialUser, Long> {

	List<TrialUser> findByIsDeletedFalse();
	TrialUser findByEmail(String email);
	boolean existsByContactNo(String contactNo);
    List<TrialUser> findByStatus(Status status);


}
