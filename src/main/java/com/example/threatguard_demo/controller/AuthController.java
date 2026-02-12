package com.example.threatguard_demo.controller;

import com.example.threatguard_demo.models.DTO.LoginRequest;
import com.example.threatguard_demo.models.DTO.LoginResponse;
import com.example.threatguard_demo.models.DTO.ResetPasswordRequest;
import com.example.threatguard_demo.models.entities.User;
import com.example.threatguard_demo.repository.UserRepository;
import com.example.threatguard_demo.service.user.CustomUserDetailsService;
import com.example.threatguard_demo.service.user.PasswordResetService;
import com.example.threatguard_demo.utils.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private CustomUserDetailsService userDetailsService;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private PasswordResetService resetService;


    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;


    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request) {

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUserId(),
                        request.getPassword()
                )
        );

        User user = userRepository.findByUserId(request.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        UserDetails userDetails =
                userDetailsService.loadUserByUsername(request.getUserId());

        String token = jwtUtil.generateToken(userDetails);

        return ResponseEntity.ok(
                new LoginResponse(token, user.getRole().name())
        );
    }


    @PostMapping("/request-reset")
    public String requestReset(@RequestParam String email) {

        resetService.sendResetEmail(email);

        return "Password reset email sent";
    }

    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestBody ResetPasswordRequest request) {

        try {

            String token = request.getToken();

            if (token == null || token.isBlank()) {
                return ResponseEntity
                        .badRequest()
                        .body(Map.of("message", "Token is missing"));
            }

            String userId = jwtUtil.extractUsername(token);

            User user = userRepository.findByUserId(userId)
                    .orElseThrow(() -> new RuntimeException("Invalid token"));

            // Validate token properly
            if (!jwtUtil.validatePasswordResetToken(token, userId)) {
                return ResponseEntity
                        .status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("message", "Token expired or invalid"));
            }

            user.setPassword(
                    passwordEncoder.encode(request.getNewPassword())
            );

            user.setPasswordResetRequired(false);

            userRepository.save(user);

            return ResponseEntity.ok(
                    Map.of("message", "Password successfully reset")
            );

        } catch (Exception e) {

            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "Reset failed or token expired"));
        }
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@RequestParam String email) {

        resetService.sendResetEmail(email);

        return ResponseEntity.ok(
                Map.of("message",
                        "If the email exists, a reset link has been sent.")
        );
    }




}
