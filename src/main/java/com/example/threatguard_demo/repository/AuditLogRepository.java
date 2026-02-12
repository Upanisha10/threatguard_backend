package com.example.threatguard_demo.repository;

import com.example.threatguard_demo.models.entities.AuditLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {
}

