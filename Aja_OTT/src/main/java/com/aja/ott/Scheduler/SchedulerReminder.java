package com.aja.ott.Scheduler;

import com.aja.ott.entity.OrientationUserAppointment;
import com.aja.ott.repository.OrientationUserAppointmentRepositry;
import com.aja.ott.service.EmailService;
import com.aja.ott.service.OrientationUserService;
import jakarta.mail.MessagingException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.time.LocalDate;
import java.util.List;

@Slf4j
@Component
public class SchedulerReminder {

    @Autowired
    private OrientationUserService orientationUserService;
    
    @Autowired
    private OrientationUserAppointmentRepositry orientationUserAppointmentRepositry;
    
    @Autowired
    private EmailService emailService;


    // Auto Run Scheduler - Every day at 10 AM
    @Scheduled(cron = "0 0 10 * * ?")
    public void checkFollowUpDateUsersScheduler() {
        try {
            log.info("Scheduler Triggered: Checking Follow-up Date Users");
            orientationUserService.checkFollowUpUsers();
        } catch (MessagingException | IOException e) {
            log.error("Error occurred while checking follow-up date users: {}", e.getMessage(), e);
        }
    }
    
    @Scheduled(cron = "0 0 10 * * *") // Every day at 10 AM
    public void sendTrialReminders() throws MessagingException {
        LocalDate today = LocalDate.now();
        List<OrientationUserAppointment> users = orientationUserAppointmentRepositry.findAll();
        String hrSubject = "Reminder:Follow-Up Scheduled for Tommorow";
        StringBuilder hrEmailContentBuilder = new StringBuilder();
        for (OrientationUserAppointment user : users) {
            if (user.getDate() != null &&
                today.equals(user.getDate().minusDays(1))&&
                !user.isDeleted()) {
            	
                String subject = "Reminder: Your Appointment with AJA is Approaching!";
                try {
                    String htmlContent = loadEmailTemplate();
                    htmlContent = htmlContent.replace("${candidateName}", user.getCandidateName())
                                             .replace("${date}", user.getDate().toString())
                                             .replace("${time}", user.getTime().toString());

                    emailService.sendEmail(user.getEmail(), subject, htmlContent);
                } catch (IOException e) {
                    log.error("Failed to load email HTML content: {}", e.getMessage(), e);
                } 
                
                hrEmailContentBuilder.append("<p><strong>Candidate:</strong> ")
                .append(user.getCandidateName())
                .append("<br><strong>Date:</strong> ")
                .append(user.getDate())
                .append("<br><strong>Time:</strong> ")
                .append(user.getTime())
                .append("<br><strong>Phone:</strong> ")
                .append(user.getPhone())
                .append("</p><hr>");
                                           
               }            
        }
        if (hrEmailContentBuilder.length() > 0) {
            try {
                String hrHtmlTemplate = loadEmailTemplateHR();
                String finalHrContent = hrHtmlTemplate.replace("${body}", hrEmailContentBuilder.toString());

                emailService.sendEmail("shaikkhadar11@gmail.com", hrSubject, finalHrContent);
            } catch (IOException e) {
                log.error("Failed to load HR email template: {}", e.getMessage(), e);
            }
        }
        
    }
    private String loadEmailTemplate() throws IOException {
        ClassPathResource message = new ClassPathResource("static/Email.html");
        return new String(Files.readAllBytes(message.getFile().toPath()), StandardCharsets.UTF_8);
    }
    private String loadEmailTemplateHR() throws IOException {
        ClassPathResource message = new ClassPathResource("static/HREmail.html");
        return new String(Files.readAllBytes(message.getFile().toPath()), StandardCharsets.UTF_8);
    }


}

