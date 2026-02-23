package com.example.threatguard_demo.service.analytics;

import com.example.threatguard_demo.models.DTO.*;
import com.example.threatguard_demo.repository.EventRepository;
import com.example.threatguard_demo.repository.SessionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AnalyticsService {

    @Autowired
    private EventRepository eventRepo;
    @Autowired
    private SessionRepository sessionRepo;


    public List<AttackTrend> getAttackTrend() {
        return eventRepo.getAttackTrend().stream()
                .map(r -> new AttackTrend(r[0].toString(), (Long) r[1]))
                .toList();
    }

    public List<CountryCount> getCountryDistribution() {
        return eventRepo.getCountryDistribution().stream()
                .map(r -> new CountryCount((String) r[0], (Long) r[1]))
                .toList();
    }

    public List<AsnCount> getTopAsn() {
        return sessionRepo.getTopAsn().stream()
                .map(r -> new AsnCount((String) r[0], (Long) r[1]))
                .toList();
    }

    public List<RiskDistribution> getRiskDistribution() {
        return eventRepo.getRiskDistribution().stream()
                .map(r -> new RiskDistribution((String) r[0], (Long) r[1]))
                .toList();
    }

    public Long getActiveSessionCount() {
        return sessionRepo.countActiveSessions();
    }
    public Long getTotalAttacks() {
        return eventRepo.count();
    }
    public Long getCriticalCount() {
        return eventRepo.countBySeverity("critical");
    }

    public List<AttackTypeCount> getAttackTypeDistribution() {
        return eventRepo.getAttackTypeDistribution().stream()
                .map(r -> new AttackTypeCount((String) r[0], (Long) r[1]))
                .toList();
    }

    public Double getAverageRiskScore() {
        return eventRepo.getAverageRiskScore();
    }



}
