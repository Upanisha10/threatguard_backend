package com.example.threatguard_demo.service.audit;

import com.example.threatguard_demo.constants.AuditAction;
import com.example.threatguard_demo.models.entities.AuditLogEntity;
import com.example.threatguard_demo.repository.AuditLogRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class AuditLogService {

    private final AuditLogRepository auditLogRepository;

    public AuditLogService(AuditLogRepository auditLogRepository) {
        this.auditLogRepository = auditLogRepository;
    }

    public void log(
            AuditAction action,
            String entityType,
            String entityId,
            String performedBy,
            String role,
            String ipAddress,
            String endpoint,
            boolean success
    ) {

        AuditLogEntity log = new AuditLogEntity();

        log.setAction(action.name());
        log.setStatus(success ? "SUCCESS" : "FAILURE");

        log.setEntityType(entityType);
        log.setEntityId(entityId);

        log.setPerformedBy(performedBy);
        log.setPerformedByRole(role);

        log.setIpAddress(ipAddress);
        log.setEndpoint(endpoint);

        log.setTimestamp(LocalDateTime.now());

        auditLogRepository.save(log);
    }
}
