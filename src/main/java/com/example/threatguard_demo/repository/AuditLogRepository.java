package com.example.threatguard_demo.repository;

import com.example.threatguard_demo.models.entities.AuditLogEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AuditLogRepository extends JpaRepository<AuditLogEntity, Long> {

    List<AuditLogEntity> findAllByOrderByTimestampDesc();

    List<AuditLogEntity> findByPerformedByOrderByTimestampDesc(String username);

    List<AuditLogEntity> findByActionOrderByTimestampDesc(String action);
}


