package com.example.threatguard_demo.service.events;

import com.example.threatguard_demo.models.DTO.MLResult;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Service
public class MLAnalysisService {

    private final RestTemplate restTemplate = new RestTemplate();

    // Your Flask ML API endpoint
    private final String ML_API_URL = "http://localhost:5050/predict";

    /**
     * Analyse a raw payload string using the ML API.
     *
     * @param payload Raw text payload
     * @return MLResult containing attackType, riskScore, and severity
     */
    public MLResult analyse(String payload) {

        try {
            // Prepare request body for Flask API
            Map<String, String> body = Map.of(
                    "payload", payload
            );

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<Map<String, String>> request =
                    new HttpEntity<>(body, headers);

            // Call the Flask ML API
            ResponseEntity<Map> response =
                    restTemplate.postForEntity(
                            ML_API_URL,
                            request,
                            Map.class
                    );

            Map result = response.getBody();

            if (result == null) {
                // fallback if API fails
                return new MLResult("UNKNOWN", 0.0, "LOW");
            }

            // Extract prediction
            String prediction = result.get("prediction").toString();

            // Extract risk score
            Double riskScore = 0.0;
            Object riskObj = result.get("risk_score");
            if (riskObj instanceof Number) {
                // Convert from % to 0.0–1.0
                riskScore = ((Number) riskObj).doubleValue() / 100.0;
            }

            // Determine severity based on riskScore
            String severity;
            if (riskScore >= 0.8) severity = "HIGH";
            else if (riskScore >= 0.5) severity = "MEDIUM";
            else severity = "LOW";

            return new MLResult(
                    prediction.toUpperCase(),
                    riskScore,
                    severity
            );

        } catch (Exception e) {
            System.out.println("ML API ERROR: " + e.getMessage());
            return new MLResult(
                    "UNKNOWN",
                    0.0,
                    "LOW"
            );
        }
    }
}