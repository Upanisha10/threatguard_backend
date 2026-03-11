package com.example.threatguard_demo.models.DTO;
import java.time.LocalDateTime;

public class AlertDTO {

    private String id;
    private String alertTitle;
    private Double riskScore;
    private String severity;
    private LocalDateTime latestEventTime;

    public AlertDTO(String id, String alertTitle, Double riskScore, String severity, LocalDateTime latestEventTime) {
        this.id = id;
        this.alertTitle = alertTitle;
        this.riskScore = riskScore;
        this.severity = severity;
        this.latestEventTime = latestEventTime;
    }

    public String getId() { return id; }
    public Double getRiskScore() { return riskScore; }
    public String getSeverity() { return severity; }
    public LocalDateTime getLatestEventTime() { return latestEventTime; }
    public String getAlertTitle() {
        return alertTitle;
    }
}
