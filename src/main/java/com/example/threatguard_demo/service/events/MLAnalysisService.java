package com.example.threatguard_demo.service.events;

import com.example.threatguard_demo.models.DTO.MLResult;
import org.springframework.stereotype.Service;

@Service
public class MLAnalysisService {

    public MLResult analyse(String payload) {

        if (payload.contains("' OR 1=1")) {
            return new MLResult(
                    "SQL_INJECTION",
                    0.92,
                    "HIGH"
            );
        }

        return new MLResult(
                "LOGIN_ATTEMPT",
                0.30,
                "LOW"
        );
    }
}

