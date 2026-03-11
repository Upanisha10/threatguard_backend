package com.example.threatguard_demo.service.events;

import com.example.threatguard_demo.models.DTO.MLResult;
import com.example.threatguard_demo.models.entities.EventEntity;
import com.example.threatguard_demo.models.entities.SessionEntity;
import com.example.threatguard_demo.repository.EventRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class EventService {

    @Autowired
    private EventRepository eventRepo;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @Autowired
    private MLAnalysisService mlAnalysisService;

    @Autowired
    private AlertService alertService;

    public EventEntity logRawEvent(
            SessionEntity session,
            String payload
    ) {

        EventEntity event = new EventEntity();
        event.setSession(session);
        event.setTimestamp(LocalDateTime.now());
        event.setRawPayload(payload);
        event.setAnalysed(false);

        EventEntity saved = eventRepo.save(event);

        // Send raw event to frontend
        messagingTemplate.convertAndSend(
                "/topic/session/" + session.getSessionId(),
                saved
        );

        // -------- ML ANALYSIS --------

        MLResult result = mlAnalysisService.analyse(payload);

        saved.setAttackType(result.attackType());
        saved.setRiskScore(result.riskScore());
        saved.setSeverity(result.severity());
        saved.setAnalysed(true);

        String alertTitle = alertService.generateAlertTitle(saved);

        saved.setAlertTitle(alertTitle);

        EventEntity updated = eventRepo.save(saved);

        // Send analysed event to frontend
        messagingTemplate.convertAndSend(
                "/topic/session/" + session.getSessionId(),
                updated
        );

        return updated;
    }

    public void saveResponse(EventEntity event, String response) {

        event.setResponsePayload(response);

        EventEntity saved = eventRepo.save(event);

        messagingTemplate.convertAndSend(
                "/topic/session/" + event.getSession().getSessionId(),
                saved
        );
    }

    public List<EventEntity> getRecentConversation(SessionEntity session) {
        return eventRepo.findTop10BySessionOrderByTimestampDesc(session);
    }
}