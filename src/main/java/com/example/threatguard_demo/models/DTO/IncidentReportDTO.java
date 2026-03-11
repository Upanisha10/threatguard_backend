package com.example.threatguard_demo.models.DTO;

import java.util.List;

public class IncidentReportDTO {

    private String sessionId;
    private String attackerIp;
    private String attackType;
    private String severity;

    private String payload;
    private String honeypotResponse;

    private String attackExplanation;
    private String attackerIntent;
    private List<String> recommendations;

    public IncidentReportDTO(
            String sessionId,
            String attackerIp,
            String attackType,
            String severity,
            String payload,
            String honeypotResponse,
            String attackExplanation,
            String attackerIntent,
            List<String> recommendations
    ) {
        this.sessionId = sessionId;
        this.attackerIp = attackerIp;
        this.attackType = attackType;
        this.severity = severity;
        this.payload = payload;
        this.honeypotResponse = honeypotResponse;
        this.attackExplanation = attackExplanation;
        this.attackerIntent = attackerIntent;
        this.recommendations = recommendations;
    }

    public String getSessionId() { return sessionId; }
    public String getAttackerIp() { return attackerIp; }
    public String getAttackType() { return attackType; }
    public String getSeverity() { return severity; }
    public String getPayload() { return payload; }
    public String getHoneypotResponse() { return honeypotResponse; }
    public String getAttackExplanation() { return attackExplanation; }
    public String getAttackerIntent() { return attackerIntent; }
    public List<String> getRecommendations() { return recommendations; }
}
