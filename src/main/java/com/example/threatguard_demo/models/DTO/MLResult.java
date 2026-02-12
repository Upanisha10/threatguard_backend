package com.example.threatguard_demo.models.DTO;

public record MLResult(
        String attackType,
        Double riskScore,
        String severity
) {}

