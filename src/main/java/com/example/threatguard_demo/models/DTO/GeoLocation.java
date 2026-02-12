package com.example.threatguard_demo.models.DTO;

public record GeoLocation(
        String country,
        String city,
        Double latitude,
        Double longitude,
        Long asn,
        String organization
) {}

