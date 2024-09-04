package com.aurionpro.bank.service;

import java.io.File;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    public void sendPassbookEmail(String to, File passbookFile) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setTo(to);
            helper.setSubject("Your Passbook");
            helper.setText("Please find your passbook attached.");

        
            helper.addAttachment("passbook.pdf", passbookFile);

            mailSender.send(message);
        } catch (MessagingException e) {
            throw new RuntimeException("Failed to send email with attachment", e);
        }
    }
    public void sendSimpleEmail(String to, String subject, String body) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(to);
            message.setSubject(subject);
            message.setText(body);
            mailSender.send(message);
            
        } catch (Exception e) {
        	 System.err.println("Failed to send email to: " + to);
        }
    }

}
