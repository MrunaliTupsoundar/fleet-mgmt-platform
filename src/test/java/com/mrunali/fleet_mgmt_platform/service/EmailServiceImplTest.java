package com.mrunali.fleet_mgmt_platform.service;

import com.mrunali.fleet_mgmt_platform.exception.EmailSendingFailedException;
import com.mrunali.fleet_mgmt_platform.exception.InvalidEmailAddressException;
import com.mrunali.fleet_mgmt_platform.service.impl.EmailServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.mail.MailSendException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class EmailServiceImplTest {

    @Mock
    private JavaMailSender mailSender;

    @InjectMocks
    private EmailServiceImpl emailService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testSendEmail_Success() {
        // Arrange
        String to = "test@example.com";
        String subject = "Test Subject";
        String text = "Test Body";

        // Act
        emailService.sendEmail(to, subject, text);

        // Assert
        ArgumentCaptor<SimpleMailMessage> messageCaptor = ArgumentCaptor.forClass(SimpleMailMessage.class);
        verify(mailSender, times(1)).send(messageCaptor.capture());

        SimpleMailMessage sentMessage = messageCaptor.getValue();
        assertNotNull(sentMessage.getTo());
        assertEquals(1, sentMessage.getTo().length);
        assertEquals(to, sentMessage.getTo()[0]);
        assertEquals(subject, sentMessage.getSubject());
        assertEquals(text, sentMessage.getText());
    }

    @ParameterizedTest
    @ValueSource(strings = {"", "test.example.com", " "})
    public void testSendEmail_InvalidEmail(String invalidEmail) {
        assertThrows(InvalidEmailAddressException.class, () -> {
            emailService.sendEmail(invalidEmail, "Subject", "Body");
        });
        verify(mailSender, never()).send(any(SimpleMailMessage.class));
    }

    @Test
    public void testSendEmail_NullEmail() {
        assertThrows(InvalidEmailAddressException.class, () -> emailService.sendEmail(null, "Subject", "Body"));
        verify(mailSender, never()).send(any(SimpleMailMessage.class));
    }

    @Test
    public void testSendEmail_SendingFailed() {
        doThrow(new MailSendException("Failed to send")).when(mailSender).send(any(SimpleMailMessage.class));

        assertThrows(EmailSendingFailedException.class, () -> emailService.sendEmail("test@example.com", "Subject", "Body"));
    }
}