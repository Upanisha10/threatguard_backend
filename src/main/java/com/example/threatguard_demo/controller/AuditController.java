package com.example.threatguard_demo.controller;

import com.example.threatguard_demo.models.entities.AuditLogEntity;
import com.example.threatguard_demo.repository.AuditLogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/audit")
@PreAuthorize("hasRole('ADMIN')")
public class AuditController {

    @Autowired
    private AuditLogRepository auditLogRepository;

    @GetMapping
    public List<AuditLogEntity> getAllAuditLogs() {
        return auditLogRepository.findAllByOrderByTimestampDesc();
    }

    @GetMapping("/user/{username}")
    public List<AuditLogEntity> getLogsByUser(@PathVariable String username) {
        return auditLogRepository.findByPerformedByOrderByTimestampDesc(username);
    }

    @GetMapping("/action/{action}")
    public List<AuditLogEntity> getLogsByAction(@PathVariable String action) {
        return auditLogRepository.findByActionOrderByTimestampDesc(action);
    }
}
