package com.example.threatguard_demo.controller;

import com.example.threatguard_demo.models.entities.EventEntity;
import com.example.threatguard_demo.models.entities.SessionEntity;
import com.example.threatguard_demo.repository.EventRepository;
import com.example.threatguard_demo.repository.SessionRepository;
import com.example.threatguard_demo.service.sessions.SessionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/sessions")
public class SessionController {

    @Autowired
    private SessionRepository sessionRepository;

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private SessionService sessionService;

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','ANALYST')")
    public ResponseEntity<List<SessionEntity>> getAllSessions() {

        List<SessionEntity> sessions = sessionRepository.findAll();

        return ResponseEntity.ok(sessions);
    }

    @GetMapping("/{sessionId}/conversation")
    public ResponseEntity<?> getConversation(@PathVariable String sessionId) {

        UUID uuid = UUID.fromString(sessionId);

        List<EventEntity> events =
                eventRepository.findBySession_SessionIdOrderByTimestampAsc(uuid);

        return ResponseEntity.ok(events);
    }

    @PostMapping("/{id}/terminate")
    public ResponseEntity<?> terminateSession(@PathVariable UUID id) {

        SessionEntity session = sessionService.terminateSession(id);

        return ResponseEntity.ok(session);
    }
}
