package com.example.threatguard_demo.aspect;

import com.example.threatguard_demo.annotations.AuditLog;
import com.example.threatguard_demo.models.entities.User;
import com.example.threatguard_demo.service.audit.AuditLogService;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class AuditAspect {

    @Autowired
    private AuditLogService auditLogService;

    @AfterReturning(
            value = "@annotation(auditLog)",
            returning = "result"
    )
    public void logSuccess(
            JoinPoint joinPoint,
            AuditLog auditLog,
            Object result
    ) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        String userId = auth != null ? auth.getName() : "SYSTEM";
        String role = auth != null && !auth.getAuthorities().isEmpty()
                ? auth.getAuthorities().iterator().next().getAuthority()
                : "SYSTEM";

        String entityId = extractEntityId(joinPoint.getArgs());

        auditLogService.logSuccess(
                auditLog.action(),
                auditLog.entityType(),
                entityId,
                userId,
                role
        );
    }

    @AfterThrowing(
            value = "@annotation(auditLog)",
            throwing = "ex"
    )
    public void logFailure(
            JoinPoint joinPoint,
            AuditLog auditLog,
            Exception ex
    ) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        String userId = auth != null ? auth.getName() : "SYSTEM";
        String role = auth != null && !auth.getAuthorities().isEmpty()
                ? auth.getAuthorities().iterator().next().getAuthority()
                : "SYSTEM";

        String entityId = extractEntityId(joinPoint.getArgs());

        auditLogService.logFailure(
                auditLog.action(),
                auditLog.entityType(),
                entityId,
                userId,
                role
        );
    }

    private String extractEntityId(Object[] args) {
        for (Object arg : args) {
            if (arg instanceof User user) {
                return user.getUserId();
            }
        }
        return null;
    }
}
