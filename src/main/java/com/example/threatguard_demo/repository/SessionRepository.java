package com.example.threatguard_demo.repository;

import com.example.threatguard_demo.constants.SessionState;
import com.example.threatguard_demo.models.DTO.AttackLocation;
import com.example.threatguard_demo.models.entities.SessionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.web.bind.support.SessionStatus;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SessionRepository
        extends JpaRepository<SessionEntity, UUID> {

    Optional<SessionEntity>
    findTopBySourceIpAndStateOrderByLastActivityDesc(
            String sourceIp,
            SessionState state
    );

    @Query("SELECT s.organization, COUNT(s) FROM SessionEntity s GROUP BY s.organization ORDER BY COUNT(s) DESC")
    List<Object[]> getTopAsn();

    @Query("SELECT COUNT(s) FROM SessionEntity s WHERE s.state = 'ACTIVE'")
    Long countActiveSessions();

    @Query("""
    SELECT 
    s.latitude,
    s.longitude,
    s.sourceCity,
    s.sourceCountry
    
    FROM SessionEntity s
    WHERE s.latitude IS NOT NULL AND s.longitude IS NOT NULL
    """)
        List<AttackLocation> getAttackLocations();

    Optional<SessionEntity> findTopBySourceIpAndUserAgentAndStateOrderByLastActivityDesc(
            String sourceIp,
            String userAgent,
            SessionState state
    );

    List<SessionEntity> findByState(SessionState state);
}

