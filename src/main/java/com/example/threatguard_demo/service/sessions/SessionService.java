package com.example.threatguard_demo.service.sessions;

import com.example.threatguard_demo.constants.SessionState;
import com.example.threatguard_demo.models.DTO.GeoLocation;
import com.example.threatguard_demo.models.entities.SessionEntity;
import com.example.threatguard_demo.repository.SessionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class SessionService {

    private static final long TIMEOUT_MINUTES = 15;

    @Autowired
    private SessionRepository sessionRepo;

    @Autowired
    private GeoIPService geoIPService;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    /**
     * Finds an active session for an attacker or creates a new one.
     */
    public SessionEntity getOrCreateSession(String ip, String ua) {

        System.out.println("Incoming IP: " + ip);

        Optional<SessionEntity> activeSession =
                sessionRepo.findTopBySourceIpAndUserAgentAndStateOrderByLastActivityDesc(
                        ip,
                        ua,
                        SessionState.ACTIVE
                );

        if (activeSession.isPresent()) {

            SessionEntity session = activeSession.get();

            // Update activity timestamp
            session.setLastActivity(LocalDateTime.now());

            SessionEntity updated = sessionRepo.save(session);

            messagingTemplate.convertAndSend("/topic/sessions", updated);

            return updated;
        }

        // Create a new session
        SessionEntity session = new SessionEntity();

        session.setSourceIp(ip);
        session.setUserAgent(ua);
        session.setProtocol("HTTP");

        session.setStartTime(LocalDateTime.now());
        session.setLastActivity(LocalDateTime.now());

        session.setState(SessionState.NEW);
        session.setRiskScore(0.0);

        try {

            GeoLocation geo = geoIPService.lookup(ip);

            session.setSourceCountry(geo.country());
            session.setSourceCity(geo.city());
            session.setLatitude(geo.latitude());
            session.setLongitude(geo.longitude());
            session.setAsn(geo.asn());
            session.setOrganization(geo.organization());

        } catch (Exception e) {

            session.setSourceCountry("Unknown");
            session.setSourceCity("Unknown");
        }

        SessionEntity saved = sessionRepo.save(session);

        // Move to ACTIVE immediately
        saved.setState(SessionState.ACTIVE);
        saved = sessionRepo.save(saved);

        messagingTemplate.convertAndSend("/topic/sessions", saved);

        return saved;
    }

    /**
     * Terminates a session manually (for admin blocking etc.)
     */
    public void terminateSession(UUID sessionId, String reason) {

        Optional<SessionEntity> sessionOpt = sessionRepo.findById(sessionId);

        if (sessionOpt.isEmpty()) return;

        SessionEntity session = sessionOpt.get();

        session.setState(SessionState.TERMINATED);
        session.setEndTime(LocalDateTime.now());

        SessionEntity updated = sessionRepo.save(session);

        messagingTemplate.convertAndSend("/topic/sessions", updated);

        System.out.println("Session terminated: " + sessionId + " reason: " + reason);
    }

    /**
     * Automatically expires inactive sessions.
     * Runs every 2 minutes.
     */
    @Scheduled(fixedRate = 120000)
    public void expireInactiveSessions() {

        LocalDateTime expiryThreshold =
                LocalDateTime.now().minusMinutes(TIMEOUT_MINUTES);

        List<SessionEntity> activeSessions =
                sessionRepo.findByState(SessionState.ACTIVE);

        for (SessionEntity session : activeSessions) {

            if (session.getLastActivity().isBefore(expiryThreshold)) {

                session.setState(SessionState.EXPIRED);
                session.setEndTime(LocalDateTime.now());

                SessionEntity expired = sessionRepo.save(session);

                messagingTemplate.convertAndSend("/topic/sessions", expired);

                System.out.println("Session expired: " + session.getSessionId());
            }
        }
    }

    public SessionEntity terminateSession(UUID sessionId) {

        SessionEntity session = sessionRepo.findById(sessionId)
                .orElseThrow(() -> new RuntimeException("Session not found"));

        // if already terminated or expired, do nothing
        if (session.getState() == SessionState.TERMINATED ||
                session.getState() == SessionState.EXPIRED) {
            return session;
        }

        session.setState(SessionState.TERMINATED);
        session.setEndTime(LocalDateTime.now());

        SessionEntity saved = sessionRepo.save(session);

        // push update to dashboard
        messagingTemplate.convertAndSend("/topic/sessions", saved);

        return saved;
    }
}