package com.example.threatguard_demo.service.events;

import com.example.threatguard_demo.models.DTO.AlertDTO;
import com.example.threatguard_demo.repository.EventRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class AlertService {

    private final EventRepository eventRepository;

    public AlertService(EventRepository eventRepository) {
        this.eventRepository = eventRepository;
    }

    public List<AlertDTO> getSessionAlerts() {

        List<Object[]> results = eventRepository.getSessionRiskAggregation();
        List<AlertDTO> alerts = new ArrayList<>();

        for (Object[] row : results) {

            UUID sessionUuid = (UUID) row[0];
            String sessionId = sessionUuid.toString();
            Double maxRiskNormalized = (Double) row[1];
            Double maxRiskPercentage = maxRiskNormalized * 100;

            LocalDateTime latestTime = (LocalDateTime) row[2];

            String severity = mapSeverity(maxRiskPercentage);

            alerts.add(
                    new AlertDTO(sessionId, maxRiskPercentage, severity, latestTime)
            );
        }

        return alerts;
    }

    private String mapSeverity(Double score) {

        if (score >= 80) return "critical";
        if (score >= 60) return "high";
        if (score >= 40) return "medium";
        return "low";
    }
}