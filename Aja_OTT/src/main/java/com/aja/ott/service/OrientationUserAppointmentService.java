package com.aja.ott.service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import com.aja.ott.constants.Constants;
import com.aja.ott.entity.OrientationUserAppointment;
import com.aja.ott.exception.CustomDatabaseException;
import com.aja.ott.repository.OrientationUserAppointmentRepositry;

import jakarta.mail.MessagingException;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class OrientationUserAppointmentService {


	@Autowired
	Constants constants;
	
	@Autowired
    OrientationUserAppointmentRepositry orientationUserAppointmentRepositry;

	@Autowired
	EmailService emailService;

	public String saveUserAppointment(OrientationUserAppointment orientationUserAppointment) throws IOException {
		log.info("Saving user appointment for candidate: {}", orientationUserAppointment.getCandidateName());

		ClassPathResource resource = new ClassPathResource("static/ConformationEmailUser.html");
		String htmlContent = new String(Files.readAllBytes(resource.getFile().toPath()), StandardCharsets.UTF_8);
		String candidateName = Optional.ofNullable(orientationUserAppointment.getCandidateName()).orElse("N/A");
		String date = orientationUserAppointment.getDate() != null ? orientationUserAppointment.getDate().toString()
				: "N/A";
		String time = orientationUserAppointment.getTime() != null ? orientationUserAppointment.getTime().toString()
				: "N/A";

		htmlContent = htmlContent.replace("${candidateName}", candidateName).replace("${date}", date).replace("${time}",
				time);

		String subject = "Slot Confirmation";

		ClassPathResource hrResource = new ClassPathResource("static/ConformationEmailHR.html");
		String hrHtmlContent = new String(Files.readAllBytes(hrResource.getFile().toPath()), StandardCharsets.UTF_8);
		hrHtmlContent = hrHtmlContent.replace("${candidateName}", candidateName).replace("${date}", date)
				.replace("${time}", time);

		log.debug("Sending email to candidate: {}", orientationUserAppointment.getEmail());
		emailService.sendEmail(orientationUserAppointment.getEmail(), subject, htmlContent);

		log.debug("Sending email to HR: EX@ajacs.in");
		emailService.sendEmail(constants.HR_EMAIL, subject, hrHtmlContent);

		try {
			orientationUserAppointment.setStatus("Scheduled");
			orientationUserAppointmentRepositry.save(orientationUserAppointment);
			log.info("User appointment saved successfully for candidate: {}", candidateName);
			return "User appointment saved successfully!";
		} catch (DataAccessException ex) {
			log.error("Database error occurred", ex);
			throw new CustomDatabaseException("Unable to save appointment", ex);
		}
	}

	public String updateUserAppointment(Long Id, OrientationUserAppointment orientationUserAppointment)
			throws IOException {
		log.info("Updating user appointment with ID: {}", Id);
		Optional<OrientationUserAppointment> existingAppointmentOptional = orientationUserAppointmentRepositry
				.findById(Id);

		if (existingAppointmentOptional.isPresent()) {
			OrientationUserAppointment existingAppointment = existingAppointmentOptional.get();
			existingAppointment.setCandidateName(orientationUserAppointment.getCandidateName());
			existingAppointment.setEmail(orientationUserAppointment.getEmail());
			existingAppointment.setPhone(orientationUserAppointment.getPhone());
			existingAppointment.setDate(orientationUserAppointment.getDate());
			existingAppointment.setTime(orientationUserAppointment.getTime());
			existingAppointment.setAddress(orientationUserAppointment.getAddress());
			existingAppointment.setStatus("Rescheduled");

			ClassPathResource resource = new ClassPathResource("static/UpdateEmailUser.html");
			String htmlContent = new String(Files.readAllBytes(resource.getFile().toPath()), StandardCharsets.UTF_8);
			htmlContent = htmlContent.replace("${candidateName}", existingAppointment.getCandidateName())
					.replace("${date}", existingAppointment.getDate().toString())
					.replace("${time}", existingAppointment.getTime().toString());

			String subject = "Update Confirmation";

			ClassPathResource hrResource = new ClassPathResource("static/UpdateEmailHR.html");
			String hrHtmlContent = new String(Files.readAllBytes(hrResource.getFile().toPath()),
					StandardCharsets.UTF_8);
			hrHtmlContent = hrHtmlContent.replace("${candidateName}", existingAppointment.getCandidateName())
					.replace("${date}", existingAppointment.getDate().toString())
					.replace("${time}", existingAppointment.getTime().toString());

			log.debug("Sending email to candidate: {}", existingAppointment.getEmail());
			emailService.sendEmail(existingAppointment.getEmail(), subject, htmlContent);
			log.debug("Sending email to HR: EX@ajacs.in");
			emailService.sendEmail(Constants.HR_EMAIL, subject, hrHtmlContent);


            try {
                orientationUserAppointmentRepositry.save(existingAppointment);
                log.info("User appointment updated successfully for ID: {}", Id);
                return "User appointment updated successfully!";
            } catch (DataAccessException ex) {
                log.error("Database error occurred", ex);
                throw new CustomDatabaseException("Unable to update the appointment", ex);
            }
        }

        log.warn("User appointment not found for ID: {}", Id);
        return "User appointment not found!";
    }

    public List<OrientationUserAppointment> getAll() {
        try {
            log.info("Fetching all non-deleted orientation users.");
            return orientationUserAppointmentRepositry.findByIsDeletedFalse();
        } catch (DataAccessException ex) {
            log.error("Database error occurred", ex);
            throw new CustomDatabaseException("Unable to fetch the data");
        }
    }

    public Optional<OrientationUserAppointment> detailsBasedOnId(Long id) {
        log.info("Fetching appointment details for ID: {}", id);
        return orientationUserAppointmentRepositry.findById(id);
    }

    public boolean softDeleteUser(Long id) {
        log.info("Soft deleting appointment with ID: {}", id);
        Optional<OrientationUserAppointment> userOptional = orientationUserAppointmentRepositry.findById(id);

        if (userOptional.isPresent()) {
            OrientationUserAppointment appointment = userOptional.get();
            if (appointment.isDeleted()) {
                log.warn("User appointment already soft deleted for ID: {}", id);
                return false;
            }
            appointment.setDeleted(true);
            try {
                orientationUserAppointmentRepositry.save(appointment);
                log.info("User appointment soft deleted successfully for ID: {}", id);
                return true;
            } catch (DataAccessException ex) {
                log.error("Database error occurred", ex);
                throw new CustomDatabaseException("Unable to delete appointment");
            }
        }

        log.warn("User appointment not found for soft delete with ID: {}", id);
        return false;
    }

    public boolean cancelUserAppointment(Long id)  throws IOException {
        log.info("Cancelling user appointment with ID: {}", id);
        Optional<OrientationUserAppointment> appointmentOpt = orientationUserAppointmentRepositry.findById(id);
        if (appointmentOpt.isPresent()) {
            OrientationUserAppointment appointment = appointmentOpt.get();
            appointment.setStatus("Cancelled");
            try {
                orientationUserAppointmentRepositry.save(appointment);
                CancelEmail(appointment);
                log.info("Appointment cancelled and soft deleted for ID: {}", id);
                return true;
            } catch (DataAccessException ex) {
                log.error("Database error occurred while cancelling", ex);
                throw new CustomDatabaseException("Unable to cancel appointment", ex);
            }
          
        }

        log.warn("User appointment not found for cancellation with ID: {}", id);
        return false;
    }

    private void CancelEmail(OrientationUserAppointment appointment) throws IOException {

        ClassPathResource resource = new ClassPathResource("static/CancleAppointmentUser.html");
        String htmlContent = new String(Files.readAllBytes(resource.getFile().toPath()), StandardCharsets.UTF_8);
        htmlContent =htmlContent.replace("${candidateName}",appointment.getCandidateName())
        						.replace("${date}",appointment.getDate().toString())
        						.replace("${time}",appointment.getTime().toString());



        String subject = "Slot Has Been Canceled";

        emailService.sendEmail(appointment.getEmail(), subject, htmlContent);


        ClassPathResource hrResource = new ClassPathResource("static/CancleAppointmentHR.html");
        String hrHtmlContent = new String(Files.readAllBytes(hrResource.getFile().toPath()), StandardCharsets.UTF_8);
        hrHtmlContent =hrHtmlContent.replace("${candidateName}",appointment.getCandidateName())
        						.replace("${date}",appointment.getDate().toString())
        						.replace("${time}",appointment.getTime().toString());



        String hrSubject = "Slot Has Been Canceled";

        emailService.sendEmail(constants.HR_EMAIL, hrSubject, hrHtmlContent);

    }


	public List<OrientationUserAppointment> getUpcomingAppointments(LocalDate startDate, LocalDate endDate) {
		try {
			log.info("Fetching appointments from {} to {} for department: {}", startDate, endDate);
			return orientationUserAppointmentRepositry.findByDateBetweenAndIsDeletedFalseOrderByDateAsc(startDate,
					endDate);
		} catch (DataAccessException ex) {
			log.error("Error fetching appointments for department: {}", ex);
			throw new CustomDatabaseException("Unable to fetch upcoming appointments", ex);
		}
	}
}
