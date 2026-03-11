package com.example.threatguard_demo.service.reports;

import com.example.threatguard_demo.models.DTO.IncidentReportDTO;
import com.example.threatguard_demo.models.entities.EventEntity;
import com.example.threatguard_demo.models.entities.SessionEntity;
import com.example.threatguard_demo.repository.EventRepository;
import com.example.threatguard_demo.repository.SessionRepository;
import com.example.threatguard_demo.service.ML.LLMService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class IncidentReportService {

    @Autowired
    private SessionRepository sessionRepository;

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private LLMService llmService;

    public IncidentReportDTO generateReport(UUID sessionId) throws Exception {

        SessionEntity session = sessionRepository
                .findById(sessionId)
                .orElseThrow();

        List<EventEntity> events =
                eventRepository.findTop10BySessionOrderByTimestampDesc(session);

        EventEntity latestEvent = events.get(0);

        Map<String, Object> analysis =
                llmService.generateSecurityAnalysis(
                        latestEvent.getAttackType(),
                        latestEvent.getRawPayload()
                );

        return new IncidentReportDTO(
                session.getSessionId().toString(),
                session.getSourceIp(),
                latestEvent.getAttackType(),
                latestEvent.getSeverity(),
                latestEvent.getRawPayload(),
                latestEvent.getResponsePayload(),
                (String) analysis.get("attackExplanation"),
                (String) analysis.get("attackerIntent"),
                (List<String>) analysis.get("recommendations")
        );
    }
}
