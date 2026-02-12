package com.example.threatguard_demo.service.events;

import com.example.threatguard_demo.models.DTO.MLResult;
import com.example.threatguard_demo.models.entities.EventEntity;
import com.example.threatguard_demo.models.entities.SessionEntity;
import com.example.threatguard_demo.repository.EventRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class EventService {

    @Autowired
    private EventRepository eventRepo;

    public EventEntity logRawEvent(
            SessionEntity session,
            String payload
    ) {
        EventEntity event = new EventEntity();
        event.setSession(session);
        event.setTimestamp(LocalDateTime.now());
        event.setRawPayload(payload);
        event.setAnalysed(false);

        return eventRepo.save(event);
    }

    public EventEntity updateWithMLResult(
            EventEntity event,
            MLResult result
    ) {
        event.setAttackType(result.attackType());
        event.setRiskScore(result.riskScore());
        event.setSeverity(result.severity());
        event.setAnalysed(true);

        return eventRepo.save(event);
    }

    public void saveResponse(EventEntity event, String response) {
        event.setResponsePayload(response);
        eventRepo.save(event);
    }

    public List<EventEntity> getRecentConversation(SessionEntity session) {
        return eventRepo.findTop10BySessionOrderByTimestampDesc(session);
    }


}
