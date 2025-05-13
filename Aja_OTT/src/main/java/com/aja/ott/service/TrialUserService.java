package com.aja.ott.service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import com.aja.ott.entity.OrientationUser;
import com.aja.ott.entity.TrialExtendStatus;
import com.aja.ott.entity.TrialUser;
import com.aja.ott.exception.CustomDatabaseException;
import com.aja.ott.exception.ResourceNotFoundException;
import com.aja.ott.exception.UserAlreadyExistsException;
import com.aja.ott.repository.TrialUserRepository;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class TrialUserService {

	@Autowired
	private TrialUserRepository trialUserRepository;

	public long getNumberofdaysLeft(Long id) throws ResourceNotFoundException {
		log.info("Fetching number of days left for TrialUser with ID: {}", id);
		TrialUser trialUser = trialUserRepository.findById(id)
			    .orElseThrow(() -> new ResourceNotFoundException("TrialUser not found with ID: " + id));
		LocalDate currentDate = LocalDate.now();
		long daysLeft = ChronoUnit.DAYS.between(currentDate, trialUser.getTrialEndDate());
		log.info("Days left for TrialUser ID {}: {}", id, daysLeft);
		return Math.max(daysLeft, 0);
	}

	public List<TrialUser> getAll() {
		try {
			log.info("Fetching all Trial users (excluding soft deleted)");
			return trialUserRepository.findByIsDeletedFalse();
			}
			catch (DataAccessException ex) {
			    log.error("Database error occurred", ex);
			    throw new CustomDatabaseException("Unable to get the Trial Users");
			}

	}
	
	public List<TrialUser> getTrialUsersByStatus(TrialUser.Status status) {
	    return trialUserRepository.findByStatus(status);
	}
	public TrialUser updateTrialUser(long id, TrialUser trialUserDetails) {
		log.info("Updating TrialUser with ID: {}", id);
		TrialUser trialUser = trialUserRepository.findById(id)
				.orElseThrow(() -> new RuntimeException("no record found"));
		log.debug("Existing user data before update: {}", trialUser);
		trialUser.setName(trialUserDetails.getName());
		trialUser.setContactNo(trialUserDetails.getContactNo());
		trialUser.setTechnology(trialUserDetails.getTechnology());
		trialUser.setEmail(trialUserDetails.getEmail());
		trialUser.setOrientationDate(trialUserDetails.getOrientationDate());
		trialUser.setOrientationGivenBy(trialUserDetails.getOrientationGivenBy());
		trialUser.setHasTechnologyTraining(trialUserDetails.isHasTechnologyTraining());
		trialUser.setParentGuardianName(trialUserDetails.getParentGuardianName());
		trialUser.setParentGuardianRelation(trialUserDetails.getParentGuardianRelation());
		trialUser.setParentGuardianContact(trialUserDetails.getParentGuardianContact());
		trialUser.setReferralSource(trialUserDetails.getReferralSource());
		trialUser.setPreviousCompanyName(trialUserDetails.getPreviousCompanyName());
		trialUser.setEmploymentStartDate(trialUserDetails.getEmploymentStartDate());
		trialUser.setEmploymentEndDate(trialUserDetails.getEmploymentEndDate());
		trialUser.setIndustry(trialUserDetails.getIndustry());
		trialUser.setInstituteName(trialUserDetails.getInstituteName());
		trialUser.setDegreeOrDiploma(trialUserDetails.getDegreeOrDiploma());
		trialUser.setSpecialization(trialUserDetails.getSpecialization());
		trialUser.setDateOfCompletion(trialUserDetails.getDateOfCompletion());
		trialUser.setInterestedToJoin(trialUserDetails.isInterestedToJoin());
		trialUser.setTrialPeriodInterest(trialUserDetails.isTrialPeriodInterest());
		trialUser.setPaymentMode(trialUserDetails.getPaymentMode());
		trialUser.setDate(trialUserDetails.getDate());
		trialUser.setSignature(trialUserDetails.getSignature());
		trialUser.setTrialStartDate(trialUserDetails.getTrialStartDate());
		trialUser.setTrialEndDate(trialUserDetails.getTrialEndDate());
		trialUser.setFirstName(trialUserDetails.getFirstName());
		trialUser.setLastName(trialUserDetails.getLastName());
		trialUser.setDateOfBirth(trialUserDetails.getDateOfBirth());
		trialUser.setGender(trialUserDetails.getGender());
		trialUser.setAge(trialUserDetails.getAge());
		trialUser.setMaritalStatus(trialUserDetails.getMaritalStatus());
		trialUser.setPanCardNumber(trialUserDetails.getPanCardNumber());
		trialUser.setAadharCardNumber(trialUserDetails.getAadharCardNumber());
		trialUser.setPrimaryPhoneNumber(trialUserDetails.getPrimaryPhoneNumber());
		trialUser.setAlternativePhoneNumber(trialUserDetails.getAlternativePhoneNumber());
		trialUser.setEmergencyPhoneNumber1(trialUserDetails.getEmergencyPhoneNumber1());
		trialUser.setEmergencyPhoneNumber2(trialUserDetails.getEmergencyPhoneNumber2());
		trialUser.setPresentAddress(trialUserDetails.getPresentAddress());
        trialUser.setPermanentAddress(trialUserDetails.getPermanentAddress());
        trialUser.setAcknowledgeTermsAndConditions(trialUserDetails.getAcknowledgeTermsAndConditions());
        try {
    		TrialUser updated = trialUserRepository.save(trialUser);
    		log.info("TrialUser with ID {} updated successfully", id);
    		return updated;
    		}catch (DataAccessException ex) {
    		    log.error("Database error occurred", ex);
    		    throw new CustomDatabaseException("Unable to update");
    		}

	}

	public String saveTrialUser(TrialUser trialUser) {
		if (trialUserRepository.findByEmail(trialUser.getEmail()) != null) {
			log.warn("User already exists with email: {}", trialUser.getEmail());
			throw new UserAlreadyExistsException("User has already been registered with this email.");
		}
		if (trialUserRepository.existsByContactNo(trialUser.getContactNo())) {
			log.warn("User already exists with phone: {}", trialUser.getContactNo());
			throw new UserAlreadyExistsException("User has already been registered with this phone number.");
		}
		log.info("Saving TrialUser based on OrUser: {}", trialUser);

		try {
			TrialUser saved= trialUserRepository.save(trialUser);
			log.info("TrialUser saved successfully: {}", saved);
			return "TrialUser saved successfully!";
			}
			catch (DataAccessException ex) {
			    log.error("Database error occurred", ex);
			    throw new CustomDatabaseException("Unable to save ");
			}
	}
	
	public Optional<TrialUser> detailsBasedOnId(Long id) {
		log.info("Fetching user details for ID: {}", id);
		return trialUserRepository.findById(id);

	}

	public boolean softDeleteUser(Long id) {
		log.info("Soft delete request received for TrialUser with ID: {}", id);
		Optional<TrialUser> userOptional = trialUserRepository.findById(id);

		if (userOptional.isPresent()) {
			TrialUser appointment = userOptional.get();
			log.debug("User found: {}", appointment);
			System.out.print(appointment);
			if (appointment.isDeleted()) {
				log.warn("TrialUser ID {} already marked as deleted", id);
				return false;
			}
			appointment.setDeleted(true);
			trialUserRepository.save(appointment);
			log.info("TrialUser ID {} soft deleted", id);
			return true;
		}
		log.warn("TrialUser ID {} not found", id);
		return false;
	}
	
	public TrialUser updateTrialUserExtend(long id, TrialUser trialUserDetails) {
		log.info("Updating TrialUser with ID: {}", id);
		TrialUser trialUser = trialUserRepository.findById(id)
				.orElseThrow(() -> new RuntimeException("no record found"));
		log.debug("Existing user data before update: {}", trialUser);
		trialUser.setName(trialUserDetails.getName());
		trialUser.setContactNo(trialUserDetails.getContactNo());
		trialUser.setTechnology(trialUserDetails.getTechnology());
		trialUser.setEmail(trialUserDetails.getEmail());
		trialUser.setOrientationDate(trialUserDetails.getOrientationDate());
		trialUser.setOrientationGivenBy(trialUserDetails.getOrientationGivenBy());
		trialUser.setHasTechnologyTraining(trialUserDetails.isHasTechnologyTraining());
		trialUser.setParentGuardianName(trialUserDetails.getParentGuardianName());
		trialUser.setParentGuardianRelation(trialUserDetails.getParentGuardianRelation());
		trialUser.setParentGuardianContact(trialUserDetails.getParentGuardianContact());
		trialUser.setReferralSource(trialUserDetails.getReferralSource());
		trialUser.setPreviousCompanyName(trialUserDetails.getPreviousCompanyName());
		trialUser.setEmploymentStartDate(trialUserDetails.getEmploymentStartDate());
		trialUser.setEmploymentEndDate(trialUserDetails.getEmploymentEndDate());
		trialUser.setIndustry(trialUserDetails.getIndustry());
		trialUser.setInstituteName(trialUserDetails.getInstituteName());
		trialUser.setDegreeOrDiploma(trialUserDetails.getDegreeOrDiploma());
		trialUser.setSpecialization(trialUserDetails.getSpecialization());
		trialUser.setDateOfCompletion(trialUserDetails.getDateOfCompletion());
		trialUser.setInterestedToJoin(trialUserDetails.isInterestedToJoin());
		trialUser.setTrialPeriodInterest(trialUserDetails.isTrialPeriodInterest());
		trialUser.setPaymentMode(trialUserDetails.getPaymentMode());
		trialUser.setDate(trialUserDetails.getDate());
		trialUser.setSignature(trialUserDetails.getSignature());
		trialUser.setTrialStartDate(trialUserDetails.getTrialStartDate());
		trialUser.setTrialEndDate(trialUserDetails.getTrialEndDate());
		trialUser.setFirstName(trialUserDetails.getFirstName());
		trialUser.setLastName(trialUserDetails.getLastName());
		trialUser.setDateOfBirth(trialUserDetails.getDateOfBirth());
		trialUser.setGender(trialUserDetails.getGender());
		trialUser.setAge(trialUserDetails.getAge());
		trialUser.setMaritalStatus(trialUserDetails.getMaritalStatus());
		trialUser.setPanCardNumber(trialUserDetails.getPanCardNumber());
		trialUser.setAadharCardNumber(trialUserDetails.getAadharCardNumber());
		trialUser.setPrimaryPhoneNumber(trialUserDetails.getPrimaryPhoneNumber());
		trialUser.setAlternativePhoneNumber(trialUserDetails.getAlternativePhoneNumber());
		trialUser.setEmergencyPhoneNumber1(trialUserDetails.getEmergencyPhoneNumber1());
		trialUser.setEmergencyPhoneNumber2(trialUserDetails.getEmergencyPhoneNumber2());
		trialUser.setPresentAddress(trialUserDetails.getPresentAddress());
        trialUser.setPermanentAddress(trialUserDetails.getPermanentAddress());
        trialUser.setComment(trialUserDetails.getComment());
        trialUser.setTrialExtendStatus(TrialExtendStatus.YES);
        trialUser.setAcknowledgeTermsAndConditions(trialUserDetails.getAcknowledgeTermsAndConditions());
        try {
    		TrialUser updated= trialUserRepository.save(trialUser);
    		log.info("TrialUser updated successfully: {}", updated);
    		return updated;
    		}
    		catch (DataAccessException ex) {
    		    log.error("Database error occurred", ex);
    		    throw new CustomDatabaseException("Unable to update ");
    		}

	}
}
