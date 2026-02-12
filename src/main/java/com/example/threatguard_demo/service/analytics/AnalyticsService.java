package com.example.threatguard_demo.service.analytics;

import com.example.threatguard_demo.models.DTO.AsnCount;
import com.example.threatguard_demo.models.DTO.AttackTrend;
import com.example.threatguard_demo.models.DTO.CountryCount;
import com.example.threatguard_demo.models.DTO.RiskDistribution;
import com.example.threatguard_demo.repository.EventRepository;
import com.example.threatguard_demo.repository.SessionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AnalyticsService {

    private EventRepository eventRepo;
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
}
