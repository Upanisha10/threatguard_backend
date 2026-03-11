package com.example.threatguard_demo.service.events;

import com.example.threatguard_demo.models.DTO.AlertDTO;
import com.example.threatguard_demo.models.entities.EventEntity;
import com.example.threatguard_demo.repository.EventRepository;
import com.example.threatguard_demo.service.ML.LLMService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Service
public class AlertService {

    private final EventRepository eventRepository;

    @Autowired
    private LLMService llmService;

    public AlertService(EventRepository eventRepository) {
        this.eventRepository = eventRepository;
    }

    public List<AlertDTO> getSessionAlerts() {

        List<Object[]> results = eventRepository.getSessionRiskAggregation();
        List<AlertDTO> alerts = new ArrayList<>();


        for (Object[] row : results) {


            System.out.println("ROW DATA: " + Arrays.toString(row));

            UUID sessionUuid = (UUID) row[0];
            String sessionId = sessionUuid.toString();
            String alertTitle = (String) row[1];
            Double maxRiskNormalized = (Double) row[2];
            Double maxRiskPercentage = maxRiskNormalized * 100;

            LocalDateTime latestTime = (LocalDateTime) row[3];

            String severity = mapSeverity(maxRiskPercentage);

            alerts.add(
                    new AlertDTO(sessionId, alertTitle, maxRiskPercentage, severity, latestTime)
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

    public String generateAlertTitle(EventEntity event) {

        double score = event.getRiskScore() * 100;

        if(score < 40) {
            return "Low Risk Activity";
        }

        return llmService.generateAlertTitle(
                event.getAttackType(),
                score
        );
    }
}