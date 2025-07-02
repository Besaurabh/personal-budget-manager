package com.pbm.controller;

import com.pbm.entity.User;
import com.pbm.repository.UserRepository;
import com.pbm.service.PasswordResetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class PasswordController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordResetService resetService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // Show the forgot-password form
    @GetMapping("/forgot-password")
    public String forgotPasswordPage() {
        return "forgot-password";
    }

    // Process the forgot-password form (send reset link)
    @PostMapping("/forgot-password")
    public String processForgotPassword(@RequestParam("email") String email, Model model) {
        boolean result = resetService.sendResetToken(email);
        model.addAttribute("message", result ? "Password reset link sent!" : "User not found with that email.");
        return "forgot-password";
    }

    // Show the reset-password form (token-based)
    @GetMapping("/reset-password")  // <-- You can keep this if it's the only one now
    public String showResetForm(@RequestParam("token") String token, Model model) {
        if (!resetService.isTokenValid(token)) {
            model.addAttribute("error", "Invalid or expired token.");
            return "reset-password";
        }
        model.addAttribute("token", token);
        return "reset-password";
    }

    // Process the password reset
    @PostMapping("/reset-password")
    public String processResetPassword(@RequestParam("token") String token,
                                       @RequestParam("password") String password,
                                       Model model) {
        User user = resetService.getUserByToken(token);
        if (user == null) {
            model.addAttribute("error", "Invalid token.");
            return "reset-password";
        }

        // Save encoded password
        user.setPassword(passwordEncoder.encode(password));
        userRepository.save(user);

        // Invalidate token
        resetService.invalidateToken(token);

        return "reset-success";
    }
}
