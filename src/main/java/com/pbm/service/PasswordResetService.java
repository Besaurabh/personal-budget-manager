package com.pbm.service;

import com.pbm.entity.PasswordResetToken;
import com.pbm.entity.User;
import com.pbm.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.time.LocalDateTime;
import java.util.Optional;


@Service
public class PasswordResetService {

    private final Map<String, String> tokenStore = new ConcurrentHashMap<>();
    private final Map<String, Long> tokenExpiry = new ConcurrentHashMap<>();

    private static final long TOKEN_EXPIRY_DURATION = 15 * 60 * 1000; // 15 minutes

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // ✅ 1. Send Reset Token via Email
    public boolean sendResetToken(String email) {
        Optional<User> userOpt = userRepository.findByEmail(email);
        if (userOpt.isEmpty()) return false;

        String token = UUID.randomUUID().toString();
        tokenStore.put(token, email);
        tokenExpiry.put(token, System.currentTimeMillis() + TOKEN_EXPIRY_DURATION);

        String link = "http://localhost:9092/reset-password-token?token=" + token;

        sendResetEmail(email, link);
        return true;
    }

    // ✅ Helper: Send Email
    private void sendResetEmail(String email, String link) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setSubject("Password Reset Request");
        message.setText("Click the link below to reset your password:\n" + link +
                "\n\nNote: This link is valid for 15 minutes.");
        mailSender.send(message);
    }

    // ✅ 2. Check token validity
    public boolean isTokenValid(String token) {
        Long expiry = tokenExpiry.get(token);
        return tokenStore.containsKey(token) && expiry != null && System.currentTimeMillis() < expiry;
    }

    // ✅ 3. Get user by token
    public User getUserByToken(String token) {
        String email = tokenStore.get(token);
        if (email == null) return null;
        return userRepository.findByEmail(email).orElse(null);
    }

    // ✅ 4. Invalidate token
    public void invalidateToken(String token) {
        tokenStore.remove(token);
        tokenExpiry.remove(token);
    }

    // ✅ 5. Update password
    public void updatePassword(User user, String rawPassword) {
        user.setPassword(passwordEncoder.encode(rawPassword));
        userRepository.save(user);

        // Remove all tokens for the user (for security)
        tokenStore.entrySet().removeIf(entry -> entry.getValue().equals(user.getEmail()));
        tokenExpiry.entrySet().removeIf(entry -> entry.getValue().equals(user.getEmail()));
    }
  
    public Optional<PasswordResetToken> validateToken(String token) {
        String email = tokenStore.get(token);
        Long expiryMillis = tokenExpiry.get(token);

        if (email != null && expiryMillis != null && System.currentTimeMillis() < expiryMillis) {
            Optional<User> userOpt = userRepository.findByEmail(email);
            if (userOpt.isPresent()) {
                User user = userOpt.get();

                PasswordResetToken tokenEntity = new PasswordResetToken();
                tokenEntity.setToken(token);
                tokenEntity.setUser(user);
                tokenEntity.setExpiryDate(
                    java.time.LocalDateTime.ofEpochSecond(expiryMillis / 1000, 0, java.time.ZoneOffset.UTC)
                );

                return Optional.of(tokenEntity);
            }
        }

        return Optional.empty(); // Token invalid or expired
    }


    public Optional<User> findUserByUsername(String username) {
        return userRepository.findByUsername(username);
    }
   
    public PasswordResetToken createPasswordResetToken1(User user) {
        String token = UUID.randomUUID().toString();
        long expiryMillis = System.currentTimeMillis() + (15 * 60 * 1000); // 15 minutes

        // Store token in memory maps (your in-memory approach)
        tokenStore.put(token, user.getEmail());
        tokenExpiry.put(token, expiryMillis);

        // Create and return token object
        PasswordResetToken tokenEntity = new PasswordResetToken();
        tokenEntity.setToken(token);
        tokenEntity.setUser(user);
        tokenEntity.setExpiryDate(LocalDateTime.now().plusMinutes(15)); // Optional, for UI display or future DB use

        return tokenEntity;
    }


	public PasswordResetToken createPasswordResetToken(User user) {
		// TODO Auto-generated method stub
		return null;
	}

}
