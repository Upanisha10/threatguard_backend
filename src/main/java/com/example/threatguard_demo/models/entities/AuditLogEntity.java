package com.example.threatguard_demo.models.entities;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "audit_logs")
public class AuditLogEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String action;           // LOGIN_SUCCESS, USER_UPDATED, etc
    private String status;           // SUCCESS, FAILED

    private String entityType;       // USER, SESSION, ALERT
    private String entityId;         // ID of affected entity

    private String performedBy;
    private String performedByRole;

    private String ipAddress;

    private LocalDateTime timestamp;

    private String endpoint;

    @PrePersist
    public void prePersist() {
        this.timestamp = LocalDateTime.now();
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setPerformedBy(String performedBy) {
        this.performedBy = performedBy;
    }

    public void setPerformedByRole(String performedByRole) {
        this.performedByRole = performedByRole;
    }

    public String getPerformedByRole() {
        return performedByRole;
    }

    public void setEntityType(String entityType) {
        this.entityType = entityType;
    }

    public String getPerformedBy() {
        return performedBy;
    }

    public void setEntityId(String entityId) {
        this.entityId = entityId;
    }

    public String getEntityId() {
        return entityId;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getStatus() {
        return status;
    }

    public String getEntityType() {
        return entityType;
    }

    public String getAction() {
        return action;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public String getEndpoint() {
        return endpoint;
    }

    public void setEndpoint(String endpoint) {
        this.endpoint = endpoint;
    }
}
