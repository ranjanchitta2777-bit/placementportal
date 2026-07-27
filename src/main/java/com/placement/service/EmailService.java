package com.placement.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    // OTP Email
    public void sendOTP(String toEmail, String otp) {

        SimpleMailMessage message = new SimpleMailMessage();

        message.setTo(toEmail);
        message.setSubject("Placement Portal Login OTP");
        message.setText("Your OTP is: " + otp + "\n\nValid for 5 minutes.");

        mailSender.send(message);
    }

    // Selection Email
    public void sendSelectionEmail(String toEmail,
                                   String studentName,
                                   String company,
                                   String role) {

        SimpleMailMessage message = new SimpleMailMessage();

        message.setTo(toEmail);

        message.setSubject("🎉 Congratulations! You are Selected");

        message.setText(
                "Dear " + studentName + ",\n\n" +
                "Congratulations!\n\n" +
                "You have been SELECTED.\n\n" +
                "Company : " + company + "\n" +
                "Role : " + role + "\n\n" +
                "Please login to the Placement Portal for more details.\n\n" +
                "Best Wishes,\nPlacement Cell"
        );

        mailSender.send(message);
    }

    // Rejection Email
    public void sendRejectionEmail(String toEmail,
                                   String studentName,
                                   String company,
                                   String role) {

        SimpleMailMessage message = new SimpleMailMessage();

        message.setTo(toEmail);

        message.setSubject("Placement Portal Application Update");

        message.setText(
                "Dear " + studentName + ",\n\n" +
                "Thank you for applying.\n\n" +
                "Unfortunately, you were not selected.\n\n" +
                "Company : " + company + "\n" +
                "Role : " + role + "\n\n" +
                "Keep preparing. More opportunities are coming!\n\n" +
                "Best Wishes,\nPlacement Cell"
        );

        mailSender.send(message);
    }
}