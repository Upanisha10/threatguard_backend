package com.example.threatguard_demo.service.analytics;

import com.example.threatguard_demo.models.DTO.DashboardOverviewDTO;
import com.example.threatguard_demo.repository.EventRepository;
import com.example.threatguard_demo.repository.SessionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class DashboardService {

    @Autowired
    private SessionRepository sessionRepository;

    @Autowired
    private EventRepository eventRepository;

    public DashboardOverviewDTO getOverview() {

        DashboardOverviewDTO dto = new DashboardOverviewDTO();

        LocalDateTime since = LocalDateTime.now().minusHours(24);

        dto.setTotalSessions(
                sessionRepository.countSessionsSince(since)
        );

        dto.setActiveThreats(
                sessionRepository.countActiveSessions()
        );

        dto.setBlockedAttacks(
                sessionRepository.countTerminatedSessions()
        );

        List<Object[]> severityCounts =
                eventRepository.countEventsBySeverity();

        Map<String, Long> threatStatus = new HashMap<>();

        for (Object[] row : severityCounts) {

            String severity = (String) row[0];
            Long count = (Long) row[1];

            threatStatus.put(severity.toLowerCase(), count);
        }

        dto.setThreatStatus(threatStatus);

        List<Object[]> recent = eventRepository.findRecentEvents();

        List<Map<String, Object>> activity = new ArrayList<>();

        for (Object[] row : recent) {

            Map<String, Object> item = new HashMap<>();

            item.put("title", row[0]);
            item.put("severity", row[1]);
            item.put("timestamp", row[2]);

            activity.add(item);
        }

        dto.setRecentActivity(activity);

        dto.setHighRiskAlerts(
                threatStatus.getOrDefault("critical", 0L)
        );

        return dto;
    }
}