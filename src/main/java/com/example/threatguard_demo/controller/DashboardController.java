package com.example.threatguard_demo.controller;

import com.example.threatguard_demo.models.DTO.DashboardOverviewDTO;
import com.example.threatguard_demo.service.analytics.DashboardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    @Autowired
    private DashboardService dashboardService;

    @GetMapping("/overview")
    public DashboardOverviewDTO getDashboardOverview() {
        return dashboardService.getOverview();
    }
}