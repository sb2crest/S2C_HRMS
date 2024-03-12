package com.employee.management.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.SimpleMailMessage;


import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
public class EmailSenderServiceTest {

    @InjectMocks
    private EmailSenderService emailSenderService;

    @Mock
    private JavaMailSender javaMailSender;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testSendSimpleEmail() {
        String toEmail = "test@example.com";
        String subject = "Test Subject";
        String body = "Test Body";

        emailSenderService.sendSimpleEmail(toEmail, subject, body);

        verify(javaMailSender).send(any(SimpleMailMessage.class));
    }
    @Test
    public void testSendSimpleEmail_whenEmailIsInvalid(){
        String toEmail = null;
        String subject = "Test Subject";
        String body = "Test Body";
        emailSenderService.sendSimpleEmail(toEmail, subject, body);
    }

}
