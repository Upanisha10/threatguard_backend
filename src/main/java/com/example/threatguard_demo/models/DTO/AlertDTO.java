package com.example.threatguard_demo.models.DTO;
import java.time.LocalDateTime;

public class AlertDTO {

    private String sessionId;
    private Double riskScore;
    private String severity;
    private LocalDateTime latestEventTime;

    public AlertDTO(String sessionId, Double riskScore, String severity, LocalDateTime latestEventTime) {
        this.sessionId = sessionId;
        this.riskScore = riskScore;
        this.severity = severity;
        this.latestEventTime = latestEventTime;
    }

    public String getSessionId() { return sessionId; }
    public Double getRiskScore() { return riskScore; }
    public String getSeverity() { return severity; }
    public LocalDateTime getLatestEventTime() { return latestEventTime; }
}
