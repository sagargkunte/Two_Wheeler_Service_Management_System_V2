package com.twowheeler.controller;

import com.twowheeler.dto.AuthDtos.*;
import com.twowheeler.model.User;
import com.twowheeler.repository.UserRepository;
import com.twowheeler.service.EmailService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private EmailService emailService;

    public static final String SESSION_USER_ID = "SESSION_USER_ID";

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest request, HttpSession session) {
        try {
            if (request.getName() == null || request.getEmail() == null || request.getPassword() == null || request.getType() == null) {
                return ResponseEntity.badRequest().body(Map.of("message", "All fields are required"));
            }

            if (!List.of("owner", "mechanic").contains(request.getType())) {
                return ResponseEntity.badRequest().body(Map.of("message", "Invalid user type"));
            }

            String email = request.getEmail().trim().toLowerCase();
            if (userRepository.existsByEmail(email)) {
                return ResponseEntity.badRequest().body(Map.of("message", "Email already registered"));
            }

            // Generate 6 digit OTP
            String rawOtp = String.valueOf((int) (100000 + Math.random() * 900000));
            LocalDateTime otpExpiry = LocalDateTime.now().plusMinutes(10);

            User user = new User();
            user.setName(request.getName().trim());
            user.setEmail(email);
            user.setPhone(request.getPhone() != null ? request.getPhone().trim() : "");
            user.setPassword(passwordEncoder.encode(request.getPassword()));
            user.setType(request.getType());
            user.setVerified(false);
            user.setOtp(rawOtp);
            user.setOtpExpires(otpExpiry);

            User savedUser = userRepository.save(user);

            // Store in session if owner so they can proceed, or store email
            session.setAttribute("otpEmail", email);
            if ("owner".equalsIgnoreCase(user.getType())) {
                session.setAttribute(SESSION_USER_ID, savedUser.getId());
            }

            // Send OTP email
            emailService.sendOtpEmail(email, user.getName(), rawOtp);

            Map<String, Object> userData = new HashMap<>();
            userData.put("id", savedUser.getId());
            userData.put("_id", savedUser.getId().toString());
            userData.put("name", savedUser.getName());
            userData.put("email", savedUser.getEmail());
            userData.put("type", savedUser.getType());

            return ResponseEntity.status(HttpStatus.CREATED).body(Map.of(
                    "message", "Registered successfully. OTP sent to email.",
                    "user", userData
            ));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "Server error during registration: " + e.getMessage()));
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request, HttpSession session) {
        try {
            if (request.getEmail() == null || request.getPassword() == null) {
                return ResponseEntity.badRequest().body(Map.of("message", "Email and password are required"));
            }

            String email = request.getEmail().trim().toLowerCase();
            Optional<User> userOpt = userRepository.findByEmail(email);

            if (userOpt.isEmpty()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", "Invalid credentials"));
            }

            User user = userOpt.get();
            if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", "Invalid credentials"));
            }

            // Optional check for user type if provided
            if (request.getUserType() != null && !request.getUserType().equalsIgnoreCase(user.getType())) {
                return ResponseEntity.badRequest().body(Map.of("message", "User type mismatch. Please select the correct login type."));
            }

            // Store user in session
            session.setAttribute(SESSION_USER_ID, user.getId());

            Map<String, Object> userData = new HashMap<>();
            userData.put("id", user.getId());
            userData.put("_id", user.getId().toString());
            userData.put("name", user.getName());
            userData.put("email", user.getEmail());
            userData.put("type", user.getType());

            return ResponseEntity.ok(Map.of(
                    "message", "Logged in successfully",
                    "user", userData
            ));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "Server error during login: " + e.getMessage()));
        }
    }

    @PostMapping("/verify-otp")
    public ResponseEntity<?> verifyOtp(@RequestBody VerifyOtpRequest request, HttpSession session) {
        try {
            String email = request.getEmail() != null ? request.getEmail().trim().toLowerCase() : (String) session.getAttribute("otpEmail");
            if (email == null) {
                return ResponseEntity.badRequest().body(Map.of("message", "Email is required"));
            }

            Optional<User> userOpt = userRepository.findByEmail(email);
            if (userOpt.isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("message", "User not found"));
            }

            User user = userOpt.get();
            if (user.isVerified()) {
                return ResponseEntity.ok(Map.of("message", "Email already verified"));
            }

            if (user.getOtp() == null || !user.getOtp().equals(request.getOtp())) {
                return ResponseEntity.badRequest().body(Map.of("message", "Invalid OTP"));
            }

            if (user.getOtpExpires() != null && user.getOtpExpires().isBefore(LocalDateTime.now())) {
                return ResponseEntity.badRequest().body(Map.of("message", "OTP expired"));
            }

            user.setVerified(true);
            user.setOtp(null);
            user.setOtpExpires(null);
            userRepository.save(user);

            // Log user in automatically after OTP verification
            session.setAttribute(SESSION_USER_ID, user.getId());

            return ResponseEntity.ok(Map.of("message", "Email verified successfully"));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "Verification error: " + e.getMessage()));
        }
    }

    @PostMapping("/resend-otp")
    public ResponseEntity<?> resendOtp(@RequestBody ResendOtpRequest request, HttpSession session) {
        try {
            String email = request.getEmail() != null ? request.getEmail().trim().toLowerCase() : (String) session.getAttribute("otpEmail");
            if (email == null) {
                return ResponseEntity.badRequest().body(Map.of("message", "Email is required"));
            }

            Optional<User> userOpt = userRepository.findByEmail(email);
            if (userOpt.isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("message", "User not found"));
            }

            User user = userOpt.get();
            if (user.isVerified()) {
                return ResponseEntity.badRequest().body(Map.of("message", "Email already verified"));
            }

            String newOtp = String.valueOf((int) (100000 + Math.random() * 900000));
            user.setOtp(newOtp);
            user.setOtpExpires(LocalDateTime.now().plusMinutes(10));
            userRepository.save(user);

            emailService.sendOtpEmail(email, user.getName(), newOtp);

            return ResponseEntity.ok(Map.of("message", "OTP resent successfully to your email"));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "Resend OTP error: " + e.getMessage()));
        }
    }

    @RequestMapping(value = "/logout", method = {RequestMethod.GET, RequestMethod.POST})
    public ResponseEntity<?> logout(HttpSession session) {
        session.invalidate();
        return ResponseEntity.ok(Map.of("message", "Logged out successfully"));
    }
}
