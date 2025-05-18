package com.zm.zmbackend.services.impl;

import com.zm.zmbackend.services.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

/**
 * Implementation of the EmailService interface.
 */
@Service
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender emailSender;

    @Autowired
    public EmailServiceImpl(JavaMailSender emailSender) {
        this.emailSender = emailSender;
    }

    @Override
    public void sendSimpleMessage(String to, String subject, String text) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject(subject);
        message.setText(text);
        emailSender.send(message);
    }

    @Override
    public void sendVerificationCode(String to, String verificationCode) {
        String subject = "Your Email Verification Code";
        String text = "Your verification code is: " + verificationCode + 
                      "\n\nPlease use this code to verify your email address. " +
                      "This code will expire after 24 hours.";
        
        sendSimpleMessage(to, subject, text);
    }
}