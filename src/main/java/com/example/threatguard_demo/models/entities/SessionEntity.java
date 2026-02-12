package com.example.threatguard_demo.models.entities;

import com.example.threatguard_demo.constants.SessionState;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "sessions")
public class SessionEntity {

    @Id
    @GeneratedValue
    @Column(name = "session_id", columnDefinition = "CHAR(36)")
    private UUID sessionId;

    private String sourceIp;
    private String userAgent;
    private String protocol;

    private LocalDateTime startTime;
    private LocalDateTime lastActivity;
    private LocalDateTime endTime;

    @Enumerated(EnumType.STRING)
    private SessionState state; // ACTIVE, TIMEOUT, CLOSED

    private Double riskScore;

    private String sourceCountry;
    private String sourceCity;
    private Double latitude;
    private Double longitude;
    private Long asn;
    private String organization;



    public LocalDateTime getStartTime() {
        return startTime;
    }

    public String getProtocol() {
        return protocol;
    }

    public String getSourceIp() {
        return sourceIp;
    }

    public UUID getSessionId() {
        return sessionId;
    }

    public String getUserAgent() {
        return userAgent;
    }

    public void setSessionId(UUID sessionId) {
        this.sessionId = sessionId;
    }

    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }

    public void setSourceIp(String sourceIp) {
        this.sourceIp = sourceIp;
    }

    public void setUserAgent(String userAgent) {
        this.userAgent = userAgent;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public void setState(SessionState state) {
        this.state = state;
    }

    public Double getRiskScore() {
        return riskScore;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public LocalDateTime getLastActivity() {
        return lastActivity;
    }

    public SessionState getStatus() {
        return state;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    public void setLastActivity(LocalDateTime lastActivity) {
        this.lastActivity = lastActivity;
    }

    public void setRiskScore(Double riskScore) {
        this.riskScore = riskScore;
    }

    public Double getLatitude() {
        return latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public SessionState getState() {
        return state;
    }

    public String getSourceCity() {
        return sourceCity;
    }

    public String getSourceCountry() {
        return sourceCountry;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public void setSourceCity(String sourceCity) {
        this.sourceCity = sourceCity;
    }

    public void setSourceCountry(String sourceCountry) {
        this.sourceCountry = sourceCountry;
    }

    public Long getAsn() {
        return asn;
    }

    public String getOrganization() {
        return organization;
    }

    public void setAsn(Long asn) {
        this.asn = asn;
    }

    public void setOrganization(String organization) {
        this.organization = organization;
    }
}

