// com.pbm.controller.ContactController.java
package com.pbm.controller;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
public class ContactController {

    @Autowired
    private JavaMailSender mailSender;

    @PostMapping("/contact-message")
    public String handleContactForm(@ModelAttribute ContactForm contactForm) {

        // Compose email
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo("besaurabh28@gmail.com"); // Replace with your actual email
        message.setSubject("📨 New Contact Message from " + contactForm.getName());
        message.setText(
                "Name: " + contactForm.getName() +
                "\nEmail: " + contactForm.getEmail() +
                "\n\nMessage:\n" + contactForm.getMessage()
        );

        // Send email
        mailSender.send(message);

        // Redirect to thank you page after successful submission
        return "thankyou";

    }
    @GetMapping("/thankyou")
    public String thankYouPage() {
        return "thankyou"; // Renders templates/thankyou.html
    }


    // Inner class for contact form data binding
    @Data
    public static class ContactForm {
        @NotBlank
        private String name;

        @Email
        @NotBlank
        private String email;

        @NotBlank
        private String message;
    }
}
