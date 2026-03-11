package com.example.threatguard_demo.models.entities;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "events")
public class EventEntity {

    @Id
    @GeneratedValue
    @Column(name = "event_id", columnDefinition = "CHAR(36)")
    private UUID eventId;

    @ManyToOne
    @JoinColumn(name = "session_id")
    private SessionEntity session;

    private LocalDateTime timestamp;

    @Column(columnDefinition = "TEXT")
    private String rawPayload;

    // ML outputs
    private String attackType;     // LOGIN, SQLI, XSS, SCAN
    private Double riskScore;      // 0.0 – 1.0
    private String severity;       // LOW, MEDIUM, HIGH

    private boolean analysed;

    @Column(columnDefinition = "TEXT")
    private String responsePayload;

    private String alertTitle;


    // getters & setters

    public void setRiskScore(Double riskScore) {
        this.riskScore = riskScore;
    }

    public Double getRiskScore() {
        return riskScore;
    }

    public boolean isAnalysed() {
        return analysed;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public SessionEntity getSession() {
        return session;
    }

    public String getAttackType() {
        return attackType;
    }

    public String getRawPayload() {
        return rawPayload;
    }

    public UUID getEventId() {
        return eventId;
    }

    public String getSeverity() {
        return severity;
    }

    public void setAnalysed(boolean analysed) {
        this.analysed = analysed;
    }

    public void setEventId(UUID eventId) {
        this.eventId = eventId;
    }

    public void setAttackType(String attackType) {
        this.attackType = attackType;
    }

    public void setRawPayload(String rawPayload) {
        this.rawPayload = rawPayload;
    }

    public void setSession(SessionEntity session) {
        this.session = session;
    }

    public void setSeverity(String severity) {
        this.severity = severity;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public String getResponsePayload() {
        return responsePayload;
    }

    public void setResponsePayload(String responsePayload) {
        this.responsePayload = responsePayload;
    }

    public String getAlertTitle() {
        return alertTitle;
    }

    public void setAlertTitle(String alertTitle) {
        this.alertTitle = alertTitle;
    }
}
