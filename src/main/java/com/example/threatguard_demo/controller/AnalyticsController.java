package com.example.threatguard_demo.controller;

import com.example.threatguard_demo.models.DTO.AsnCount;
import com.example.threatguard_demo.models.DTO.AttackTrend;
import com.example.threatguard_demo.models.DTO.CountryCount;
import com.example.threatguard_demo.models.DTO.RiskDistribution;
import com.example.threatguard_demo.service.analytics.AnalyticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/analytics")
@RequiredArgsConstructor
public class AnalyticsController {

    @Autowired
    private AnalyticsService analyticsService;

    @GetMapping("/attack-trend")
    public List<AttackTrend> attackTrend() {
        return analyticsService.getAttackTrend();
    }

    @GetMapping("/country-distribution")
    public List<CountryCount> countryDistribution() {
        return analyticsService.getCountryDistribution();
    }

    @GetMapping("/top-asn")
    public List<AsnCount> topAsn() {
        return analyticsService.getTopAsn();
    }

    @GetMapping("/risk-distribution")
    public List<RiskDistribution> riskDistribution() {
        return analyticsService.getRiskDistribution();
    }

    @GetMapping("/active-sessions")
    public Long activeSessions() {
        return analyticsService.getActiveSessionCount();
    }
}

