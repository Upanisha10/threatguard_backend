package com.example.threatguard_demo.controller;

import com.example.threatguard_demo.models.DTO.IncidentReportDTO;
import com.example.threatguard_demo.service.reports.IncidentReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/reports")
public class ReportController {

    @Autowired
    private IncidentReportService reportService;

    @GetMapping("/session/{sessionId}")
    public IncidentReportDTO getReport(
            @PathVariable UUID sessionId
    ) throws Exception {

        return reportService.generateReport(sessionId);
    }
}
