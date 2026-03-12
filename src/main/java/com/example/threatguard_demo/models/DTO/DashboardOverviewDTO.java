package com.example.threatguard_demo.models.DTO;

import java.util.List;
import java.util.Map;

public class DashboardOverviewDTO {

    private Long totalSessions;
    private Long activeThreats;
    private Long highRiskAlerts;
    private Long blockedAttacks;

    private Map<String, Long> threatStatus;
    private List<Map<String, Object>> recentActivity;

    public Long getTotalSessions() {
        return totalSessions;
    }

    public void setTotalSessions(Long totalSessions) {
        this.totalSessions = totalSessions;
    }

    public Long getActiveThreats() {
        return activeThreats;
    }

    public void setActiveThreats(Long activeThreats) {
        this.activeThreats = activeThreats;
    }

    public Long getHighRiskAlerts() {
        return highRiskAlerts;
    }

    public void setHighRiskAlerts(Long highRiskAlerts) {
        this.highRiskAlerts = highRiskAlerts;
    }

    public Long getBlockedAttacks() {
        return blockedAttacks;
    }

    public void setBlockedAttacks(Long blockedAttacks) {
        this.blockedAttacks = blockedAttacks;
    }

    public Map<String, Long> getThreatStatus() {
        return threatStatus;
    }

    public void setThreatStatus(Map<String, Long> threatStatus) {
        this.threatStatus = threatStatus;
    }

    public List<Map<String, Object>> getRecentActivity() {
        return recentActivity;
    }

    public void setRecentActivity(List<Map<String, Object>> recentActivity) {
        this.recentActivity = recentActivity;
    }
}