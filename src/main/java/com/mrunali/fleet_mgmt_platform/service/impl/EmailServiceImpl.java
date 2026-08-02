package com.mrunali.fleet_mgmt_platform.service.impl;

import com.mrunali.fleet_mgmt_platform.exception.EmailSendingFailedException;
import com.mrunali.fleet_mgmt_platform.exception.InvalidEmailAddressException;
import com.mrunali.fleet_mgmt_platform.service.EmailService;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender mailSender;

    public EmailServiceImpl(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    @Override
    public void sendEmail(String to, String subject, String text) {
        if (to == null || to.isEmpty() || !to.contains("@")) {
            throw new InvalidEmailAddressException("Invalid email address");
        }

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject(subject);
        message.setText(text);

        try {
            mailSender.send(message);
        } catch (Exception e) {
            throw new EmailSendingFailedException("Failed to send email: " + e.getMessage());
        }
}
}