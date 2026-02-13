package com.example.threatguard_demo.aspect;

import com.example.threatguard_demo.annotations.AuditLog;
import com.example.threatguard_demo.constants.AuditAction;
import com.example.threatguard_demo.models.entities.User;
import com.example.threatguard_demo.repository.UserRepository;
import com.example.threatguard_demo.service.audit.AuditLogService;
import jakarta.servlet.http.HttpServletRequest;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Optional;

@Aspect
@Component
public class AuditAspect {

    private final AuditLogService auditLogService;
    private final HttpServletRequest request;
    private final UserRepository userRepository;

    public AuditAspect(AuditLogService auditLogService,
                       HttpServletRequest request,
                       UserRepository userRepository) {
        this.auditLogService = auditLogService;
        this.request = request;
        this.userRepository = userRepository;
    }

    // =========================
    // POINTCUT
    // =========================
    @Pointcut("@annotation(auditAnnotation)")
    public void auditPointcut(AuditLog auditAnnotation) {}

    // =========================
    // SUCCESS
    // =========================
    @AfterReturning(
            pointcut = "auditPointcut(auditAnnotation)",
            returning = "result"
    )
    public void logSuccess(JoinPoint joinPoint,
                           AuditLog auditAnnotation,
                           Object result) {

        log(joinPoint, auditAnnotation, true, result);
    }

    // =========================
    // FAILURE
    // =========================
    @AfterThrowing(
            pointcut = "auditPointcut(auditAnnotation)",
            throwing = "ex"
    )
    public void logFailure(JoinPoint joinPoint,
                           AuditLog auditAnnotation,
                           Exception ex) {

        log(joinPoint, auditAnnotation, false, null);
    }

    // =========================
    // CORE LOGIC
    // =========================
    private void log(JoinPoint joinPoint,
                     AuditLog auditAnnotation,
                     boolean success,
                     Object result) {

        Authentication auth =
                SecurityContextHolder.getContext().getAuthentication();

        String performedBy =
                (auth != null) ? auth.getName() : "SYSTEM";

        String role =
                (auth != null && !auth.getAuthorities().isEmpty())
                        ? auth.getAuthorities().iterator().next().getAuthority()
                        : "SYSTEM";

        String ip = request.getRemoteAddr();
        String endpoint = request.getRequestURI();

        String entityId = extractEntityId(joinPoint.getArgs(), result);

        auditLogService.log(
                auditAnnotation.action(),
                auditAnnotation.entityType(),
                entityId,
                performedBy,
                role,
                ip,
                endpoint,
                success
        );
    }

    // =========================
    // ENTITY ID EXTRACTION
    // =========================
    private String extractEntityId(Object[] args, Object result) {

        // 1️⃣ If create method returns Map containing userId
        if (result instanceof Map<?, ?> map) {
            Object userId = map.get("userId");
            if (userId != null) {
                return userId.toString();
            }
        }

        // 2️⃣ If argument is User entity
        for (Object arg : args) {

            if (arg instanceof User user) {
                return user.getUserId();
            }

            // 3️⃣ If argument is Long id → convert to user_id
            if (arg instanceof Long id) {
                Optional<User> optionalUser = userRepository.findById(id);
                if (optionalUser.isPresent()) {
                    return optionalUser.get().getUserId();
                }
            }

            // 4️⃣ If argument is already String (like login userId)
            if (arg instanceof String str) {
                return str;
            }
        }

        return "-";
    }
}
