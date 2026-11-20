package com.twowheeler.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import jakarta.mail.internet.MimeMessage;

@Service
public class EmailService {

    @Autowired(required = false)
    private JavaMailSender mailSender;

    public void sendOtpEmail(String toEmail, String recipientName, String otp) {
        String subject = "Verify Your Email - OTP";
        String htmlContent = "<div style='font-family: Arial, sans-serif; background-color: #f5f5f5; padding: 20px;'>"
                + "<div style='max-width: 600px; margin: 0 auto; background-color: #ffffff; border-radius: 10px; overflow: hidden; box-shadow: 0 4px 10px rgba(0,0,0,0.1);'>"
                + "<div style='background-color: #2563eb; padding: 20px; text-align: center; color: #ffffff; font-size: 24px; font-weight: bold;'>Two-Wheeler Service</div>"
                + "<div style='padding: 30px; text-align: center;'>"
                + "<h2>Hello " + recipientName + ",</h2>"
                + "<p>Thank you for registering with us! To complete your registration, please use the OTP below:</p>"
                + "<div style='margin: 25px 0;'>"
                + "<span style='display: inline-block; font-size: 28px; letter-spacing: 6px; background-color: #eff6ff; color: #2563eb; padding: 12px 24px; border-radius: 8px; font-weight: bold;'>"
                + otp + "</span>"
                + "</div>"
                + "<p style='color: #666; font-size: 14px;'>This OTP will expire in 10 minutes.</p>"
                + "</div>"
                + "<div style='background-color: #f8fafc; padding: 15px; text-align: center; font-size: 12px; color: #64748b;'>&copy; 2026 Two-Wheeler Service. All rights reserved.</div>"
                + "</div></div>";

        System.out.println("==================================================");
        System.out.println("OTP for " + toEmail + " is: [" + otp + "]");
        System.out.println("==================================================");

        if (mailSender != null) {
            try {
                MimeMessage message = mailSender.createMimeMessage();
                MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
                helper.setTo(toEmail);
                helper.setSubject(subject);
                helper.setText(htmlContent, true);
                mailSender.send(message);
                System.out.println("Email sent successfully to: " + toEmail);
            } catch (Exception e) {
                System.err.println("Notice: Could not send actual SMTP email (" + e.getMessage() + "), OTP logged above for quick testing.");
            }
        }
    }
}
