package com.zm.zmbackend.services;

/**
 * Service for sending emails.
 */
public interface EmailService {
    
    /**
     * Send a simple email message.
     * 
     * @param to the recipient's email address
     * @param subject the email subject
     * @param text the email body text
     */
    void sendSimpleMessage(String to, String subject, String text);
    
    /**
     * Send a verification code to the user's email.
     * 
     * @param to the recipient's email address
     * @param verificationCode the verification code to send
     */
    void sendVerificationCode(String to, String verificationCode);
}