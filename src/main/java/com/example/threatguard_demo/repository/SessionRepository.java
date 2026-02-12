package com.example.threatguard_demo.repository;

import com.example.threatguard_demo.constants.SessionState;
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



}

