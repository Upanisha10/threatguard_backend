package com.example.threatguard_demo.service.sessions;

import com.example.threatguard_demo.constants.SessionState;
import com.example.threatguard_demo.models.DTO.GeoLocation;
import com.example.threatguard_demo.models.entities.SessionEntity;
import com.example.threatguard_demo.repository.SessionRepository;
import org.springframework.beans.factory.annotation.Autowired;
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


    public SessionEntity getOrCreateSession(String ip, String ua) {

        Optional<SessionEntity> active =
                sessionRepo.findTopBySourceIpAndStateOrderByLastActivityDesc(
                        ip, SessionState.ACTIVE
                );

        if (active.isPresent()) {
            SessionEntity s = active.get();

            if (s.getLastActivity()
                    .isAfter(LocalDateTime.now().minusMinutes(TIMEOUT_MINUTES))) {

                s.setLastActivity(LocalDateTime.now());
                return sessionRepo.save(s);
            } else {
                s.setState(SessionState.TIMEOUT);
                s.setEndTime(LocalDateTime.now());
                sessionRepo.save(s);
            }
        }

        SessionEntity session = new SessionEntity();
        session.setSourceIp(ip);
        session.setUserAgent(ua);
        session.setProtocol("HTTP");
        session.setStartTime(LocalDateTime.now());
        session.setLastActivity(LocalDateTime.now());
        session.setState(SessionState.ACTIVE);
        session.setRiskScore(0.0);
        GeoLocation geo = geoIPService.lookup(ip);

        session.setSourceCountry(geo.country());
        session.setSourceCity(geo.city());
        session.setLatitude(geo.latitude());
        session.setLongitude(geo.longitude());
        session.setAsn(geo.asn());
        session.setOrganization(geo.organization());



        return sessionRepo.save(session);
    }
}

