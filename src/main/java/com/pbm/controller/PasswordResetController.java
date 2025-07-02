package com.pbm.controller;

import com.pbm.entity.PasswordResetToken;
import com.pbm.entity.User;
import com.pbm.service.PasswordResetService;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@Controller
public class PasswordResetController {

    @Autowired
    private PasswordResetService passwordResetService;

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping("/forgot-password-page")
    public String showForgotForm() {
        return "forgot-password";
    }

    @PostMapping("/forgot-password-page")
    public String processForgot(@RequestParam String username,
                                HttpServletRequest request,
                                Model model) {
        Optional<User> userOpt = passwordResetService.findUserByUsername(username);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            PasswordResetToken tokenEntity = passwordResetService.createPasswordResetToken(user);
            String resetLink = request.getRequestURL().toString().replace("/forgot-password", "")
                    + "/reset-password?token=" + tokenEntity.getToken();

            // Send email
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(user.getEmail());
            message.setSubject("Password Reset Link");
            message.setText("Click to reset your password: " + resetLink + "\n\nValid for 15 minutes only.");
            mailSender.send(message);

            model.addAttribute("message", "Reset link sent to your email.");
        } else {
            model.addAttribute("error", "User not found.");
        }

        return "forgot-password";
    }

    @GetMapping("/reset-password-action")
    public String showResetForm(@RequestParam String token, Model model) {
        Optional<PasswordResetToken> tokenOpt = passwordResetService.validateToken(token);
        if (tokenOpt.isPresent()) {
            model.addAttribute("token", token);
            return "reset-password";
        } else {
            return "reset-token-expired";
        }
    }

    @PostMapping("/reset-password-action")
    public String processReset(@RequestParam String token,
                               @RequestParam String password,
                               Model model) {
        Optional<PasswordResetToken> tokenOpt = passwordResetService.validateToken(token);
        if (tokenOpt.isPresent()) {
            User user = tokenOpt.get().getUser();
            user.setPassword(passwordEncoder.encode(password));
            passwordResetService.updatePassword(user, token);
            return "reset-success";
        } else {
            return "reset-token-expired";
        }
    }
}
