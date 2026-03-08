package com.example.threatguard_demo.service.sessions;

import com.example.threatguard_demo.constants.SessionState;
import com.example.threatguard_demo.models.DTO.GeoLocation;
import com.example.threatguard_demo.models.entities.SessionEntity;
import com.example.threatguard_demo.repository.SessionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class SessionService {

    private static final long TIMEOUT_MINUTES = 10;

    @Autowired
    private SessionRepository sessionRepo;

    @Autowired
    private GeoIPService geoIPService;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    public SessionEntity getOrCreateSession(String ip, String ua) {

        System.out.println("Incoming IP: " + ip);

        Optional<SessionEntity> active =
                sessionRepo.findTopBySourceIpAndStateOrderByLastActivityDesc(
                        ip,
                        SessionState.ACTIVE
                );

        if (active.isPresent()) {

            SessionEntity session = active.get();

            // session still active
            if (session.getLastActivity()
                    .isAfter(LocalDateTime.now().minusMinutes(TIMEOUT_MINUTES))) {

                session.setLastActivity(LocalDateTime.now());

                SessionEntity updated = sessionRepo.save(session);

                messagingTemplate.convertAndSend("/topic/sessions", updated);

                return updated;
            }

            // session timed out
            session.setState(SessionState.TIMEOUT);
            session.setEndTime(LocalDateTime.now());

            SessionEntity timedOut = sessionRepo.save(session);

            messagingTemplate.convertAndSend("/topic/sessions", timedOut);
        }

        // create new session
        SessionEntity session = new SessionEntity();

        session.setSourceIp(ip);
        session.setUserAgent(ua);
        session.setProtocol("HTTP");
        session.setStartTime(LocalDateTime.now());
        session.setLastActivity(LocalDateTime.now());
        session.setState(SessionState.ACTIVE);
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

        SessionEntity savedSession = sessionRepo.save(session);

        messagingTemplate.convertAndSend("/topic/sessions", savedSession);

        return savedSession;
    }
}