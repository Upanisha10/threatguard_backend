package com.example.threatguard_demo.controller;
import com.example.threatguard_demo.models.DTO.AlertDTO;
import com.example.threatguard_demo.models.entities.EventEntity;
import com.example.threatguard_demo.repository.EventRepository;
import com.example.threatguard_demo.service.events.AlertService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/alerts")
public class AlertsController {

    private final AlertService alertService;

    @Autowired
    private EventRepository eventRepository;

    public AlertsController(AlertService alertService) {
        this.alertService = alertService;
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('ANALYST')")
    public List<AlertDTO> getAlerts() {
        return alertService.getSessionAlerts();
    }

    @GetMapping("/{sessionId}/events")
    @PreAuthorize("hasRole('ADMIN') or hasRole('ANALYST')")
    public List<EventEntity> getSessionEvents(@PathVariable String sessionId) {

        UUID uuid = UUID.fromString(sessionId);

        return eventRepository.findBySession_SessionIdOrderByTimestampAsc(uuid);
    }


}
