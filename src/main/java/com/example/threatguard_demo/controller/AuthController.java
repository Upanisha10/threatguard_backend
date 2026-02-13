package com.example.threatguard_demo.controller;

import com.example.threatguard_demo.annotations.AuditLog;
import com.example.threatguard_demo.constants.AuditAction;
import com.example.threatguard_demo.models.DTO.LoginRequest;
import com.example.threatguard_demo.models.DTO.LoginResponse;
import com.example.threatguard_demo.models.DTO.ResetPasswordRequest;
import com.example.threatguard_demo.models.entities.User;
import com.example.threatguard_demo.repository.UserRepository;
import com.example.threatguard_demo.service.audit.AuditLogService;
import com.example.threatguard_demo.service.user.CustomUserDetailsService;
import com.example.threatguard_demo.service.user.PasswordResetService;
import com.example.threatguard_demo.utils.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
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

    @Autowired
    private AuditLogService auditLogService;

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(
            @RequestBody LoginRequest loginRequest,
            HttpServletRequest httpRequest
    ) {

        try {

            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getUserId(),
                            loginRequest.getPassword()
                    )
            );

            User user = userRepository.findByUserId(loginRequest.getUserId())
                    .orElseThrow(() -> new RuntimeException("User not found"));

            UserDetails userDetails =
                    userDetailsService.loadUserByUsername(loginRequest.getUserId());

            String token = jwtUtil.generateToken(userDetails);

            // ✅ Use httpRequest here
            auditLogService.log(
                    AuditAction.LOGIN_SUCCESS,
                    "USER",
                    user.getUserId(),
                    user.getUserId(),
                    user.getRole().name(),
                    httpRequest.getRemoteAddr(),
                    httpRequest.getRequestURI(),
                    true
            );

            return ResponseEntity.ok(
                    new LoginResponse(token, user.getRole().name())
            );

        } catch (Exception e) {

            auditLogService.log(
                    AuditAction.LOGIN_FAILURE,
                    "USER",
                    loginRequest.getUserId(),
                    loginRequest.getUserId(),
                    "UNKNOWN",
                    httpRequest.getRemoteAddr(),
                    httpRequest.getRequestURI(),
                    false
            );

            throw e;
        }
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
