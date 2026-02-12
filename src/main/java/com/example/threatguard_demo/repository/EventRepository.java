package com.example.threatguard_demo.repository;

import com.example.threatguard_demo.models.entities.EventEntity;
import com.example.threatguard_demo.models.entities.SessionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.UUID;

public interface EventRepository
        extends JpaRepository<EventEntity, UUID> {

    @Query("SELECT DATE(e.timestamp), COUNT(e) FROM EventEntity e GROUP BY DATE(e.timestamp)")
    List<Object[]> getAttackTrend();

    @Query("SELECT e.session.sourceCountry, COUNT(e) FROM EventEntity e GROUP BY e.session.sourceCountry")
    List<Object[]> getCountryDistribution();


    @Query("""
    SELECT 
    CASE 
    WHEN e.riskScore < 30 THEN 'LOW'
    WHEN e.riskScore < 70 THEN 'MEDIUM'
    ELSE 'HIGH'
    END,
    COUNT(e)
    FROM EventEntity e
    GROUP BY 
    CASE 
    WHEN e.riskScore < 30 THEN 'LOW'
    WHEN e.riskScore < 70 THEN 'MEDIUM'
    ELSE 'HIGH'
    END
    """)
    List<Object[]> getRiskDistribution();


    List<EventEntity> findTop10BySessionOrderByTimestampDesc(SessionEntity session);

    @Query("""
    SELECT e.session.sessionId,
           MAX(e.riskScore),
           MAX(e.timestamp)
    FROM EventEntity e
    GROUP BY e.session.sessionId
    """)
    List<Object[]> getSessionRiskAggregation();


    List<EventEntity> findBySession_SessionIdOrderByTimestampAsc(UUID sessionId);


}

