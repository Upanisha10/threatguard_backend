package com.example.threatguard_demo.models.DTO;

import lombok.AllArgsConstructor;
import lombok.Getter;

public class RiskDistribution {
    private String level;
    private Long count;

    public RiskDistribution(String s, Long aLong) {
    }
}

