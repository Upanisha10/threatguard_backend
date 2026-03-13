package com.example.threatguard_demo.service.user;

import com.example.threatguard_demo.annotations.AuditLog;
import com.example.threatguard_demo.constants.AuditAction;
import com.example.threatguard_demo.models.entities.User;
import com.example.threatguard_demo.repository.UserRepository;
import com.example.threatguard_demo.utils.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class PasswordResetService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private EmailService emailService;

    @AuditLog(action = AuditAction.PASSWORD_RESET_REQUEST, entityType = "USER")
    public void sendResetEmail(String email) {

        Optional<User> optionalUser = userRepository.findByEmail(email);

        if (optionalUser.isEmpty()) {
            // Do nothing — avoid user enumeration
            return;
        }

        User user = optionalUser.get();

        String token = jwtUtil.generateForgotPasswordToken(user.getUserId());

        String resetLink =
                "https://threatguard-frontend.vercel.app/reset-password?token=" + token;

        String body =
                "Hello " + user.getName() +
                        ",\n\nYou requested to reset your password.\n\n" +
                        "Click the link below to reset your password:\n" +
                        resetLink +
                        "\n\nThis link is valid for 24 hours.";

        emailService.sendEmail(
                user.getEmail(),
                "Password Reset Request",
                body
        );
    }

}
