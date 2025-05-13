package com.aja.ott.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.aja.ott.entity.OrientationUserAppointment;

@Repository
public interface OrientationUserAppointmentRepositry extends JpaRepository<OrientationUserAppointment, Long> {
	List<OrientationUserAppointment> findByIsDeletedFalse();

	List<OrientationUserAppointment> findByDateBetweenAndIsDeletedFalseOrderByDateAsc(LocalDate startDate,
			LocalDate endDate);

}
