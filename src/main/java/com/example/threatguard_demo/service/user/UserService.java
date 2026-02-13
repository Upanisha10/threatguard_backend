package com.example.threatguard_demo.service.user;

import com.example.threatguard_demo.annotations.AuditLog;
import com.example.threatguard_demo.constants.AuditAction;
import com.example.threatguard_demo.constants.Role;
import com.example.threatguard_demo.models.entities.User;
import com.example.threatguard_demo.repository.UserRepository;
import com.example.threatguard_demo.utils.JwtUtil;
import com.example.threatguard_demo.utils.UserIdGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class UserService {

    private final UserIdGenerator userIdGenerator;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;
    private final JwtUtil jwtUtil;

    @Autowired
    public UserService(UserIdGenerator userIdGenerator,
                       UserRepository userRepository,
                       PasswordEncoder passwordEncoder,
                       EmailService emailService,
                       JwtUtil jwtUtil) {

        this.userIdGenerator = userIdGenerator;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.emailService = emailService;
        this.jwtUtil = jwtUtil;
    }

    // =========================
    // CREATE USER
    // =========================

    @AuditLog(action = AuditAction.USER_CREATED, entityType = "USER")
    public ResponseEntity<?> registerUser(String name, String email, Role role) {

        User user = new User();

        user.setName(name);
        user.setEmail(email);

        String generatedId = userIdGenerator.generateUserId(name);
        user.setUserId(generatedId);

        String tempPassword = "Admin@123";
        user.setPassword(passwordEncoder.encode(tempPassword));

        user.setRole(role);
        user.setPasswordResetRequired(true);

        User savedUser = userRepository.save(user);

        boolean emailSent = false;

        try {
            String token = jwtUtil.generatePasswordResetToken(generatedId);

            String resetLink =
                    "http://localhost:5173/reset-password?token=" + token;

            String body =
                    "Hello " + name +
                            ",\n\nYour account has been created by admin.\n" +
                            "Your User ID : " + generatedId + "\n" +
                            "Temporary Password : Admin@123\n\n" +
                            "Please set your password using the link below:\n" +
                            resetLink +
                            "\n\nThis link will expire in 15 minutes.";

            emailService.sendEmail(email, "Set Your Password", body);

            emailSent = true;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return ResponseEntity.ok(
                Map.of(
                        "userCreated", true,
                        "emailSent", emailSent,
                        "userId", generatedId
                )
        );
    }


    // =========================
    // UPDATE USER (ADMIN)
    // =========================

    @AuditLog(action = AuditAction.USER_UPDATED, entityType = "USER")
    public ResponseEntity<?> updateUserById(Long id, User updatedData) {

        Optional<User> optionalUser = userRepository.findById(id);

        if (optionalUser.isEmpty()) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", "User not found"));
        }

        User existingUser = optionalUser.get();

        existingUser.setName(updatedData.getName());
        existingUser.setEmail(updatedData.getEmail());
        existingUser.setRole(updatedData.getRole());

        userRepository.save(existingUser);

        return ResponseEntity.ok(
                Map.of(
                        "message", "User updated successfully",
                        "userId", existingUser.getUserId()

                )
        );
    }


    // =========================
    // DELETE USER
    // =========================

    @AuditLog(action = AuditAction.USER_DELETED, entityType = "USER")
    public ResponseEntity<?> deleteUserById(Long id) {

        Optional<User> userOpt = userRepository.findById(id);

        if (userOpt.isEmpty()) {
            return new ResponseEntity<>("User not found", HttpStatus.NOT_FOUND);
        }

        User user = userOpt.get();   // 🔥 we load full user first

        userRepository.delete(user); // delete using entity

        return ResponseEntity.ok(
                Map.of(
                        "message", "User deleted successfully",
                        "userId", user.getUserId()  // 🔥 important
                )
        );
    }


    // =========================
    // GET ALL USERS
    // =========================

    public ResponseEntity<?> getAllUsers() {
        try {
            List<User> users = userRepository.findAll();

            if (users.isEmpty()) {
                return ResponseEntity
                        .status(HttpStatus.NO_CONTENT)
                        .body("No users found");
            }

            return ResponseEntity.ok(users);

        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error fetching users: " + e.getMessage());
        }
    }
}
