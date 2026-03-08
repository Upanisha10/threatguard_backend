package com.example.threatguard_demo.models.DTO;
public record AttackLocation(
        Double latitude,
        Double longitude,
        String city,
        String country
) {}