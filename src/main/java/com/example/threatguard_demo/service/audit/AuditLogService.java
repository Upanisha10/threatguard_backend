package com.example.threatguard_demo.service.audit;

import com.example.threatguard_demo.constants.AuditAction;
import com.example.threatguard_demo.models.entities.AuditLog;
import com.example.threatguard_demo.repository.AuditLogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AuditLogService {

    @Autowired
    private AuditLogRepository auditLogRepository;

    public void logSuccess(
            AuditAction action,
            String entityType,
            String entityId,
            String performedBy,
            String role
    ) {
        AuditLog log = new AuditLog();

        log.setAction(action.name());
        log.setStatus("SUCCESS");
        log.setEntityType(entityType);
        log.setEntityId(entityId);
        log.setPerformedBy(performedBy);
        log.setPerformedByRole(role);

        auditLogRepository.save(log);
    }

    public void logFailure(
            AuditAction action,
            String entityType,
            String entityId,
            String performedBy,
            String role
    ) {
        AuditLog log = new AuditLog();

        log.setAction(action.name());
        log.setStatus("FAILURE");
        log.setEntityType(entityType);
        log.setEntityId(entityId);
        log.setPerformedBy(performedBy);
        log.setPerformedByRole(role);

        auditLogRepository.save(log);
    }
}
