package com.example.threatguard_demo.service.ML;

import com.example.threatguard_demo.models.entities.EventEntity;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@Service
public class LLMService {

    @Value("${gemini.api.key}")
    private String apiKey;

    private static final String[] MODELS = {
            "models/gemini-2.5-flash",
            "models/gemini-2.0-flash-exp"
    };

    private static final String BASE_URL =
            "https://generativelanguage.googleapis.com/v1beta/";

    private final RestTemplate restTemplate = new RestTemplate();


    /* ============================================================
       GEMINI CALL (Reusable)
       ============================================================ */

    private String callGemini(String prompt) {

        Map<String, Object> requestBody = Map.of(
                "contents", List.of(
                        Map.of("parts", List.of(
                                Map.of("text", prompt)
                        ))
                )
        );

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Map<String, Object>> entity =
                new HttpEntity<>(requestBody, headers);

        for (String model : MODELS) {

            String url = BASE_URL + model + ":generateContent?key=" + apiKey;

            try {

                ResponseEntity<Map> response =
                        restTemplate.postForEntity(url, entity, Map.class);

                if (response.getStatusCode() == HttpStatus.OK &&
                        response.getBody() != null) {

                    List<Map<String, Object>> candidates =
                            (List<Map<String, Object>>) response.getBody().get("candidates");

                    if (candidates != null && !candidates.isEmpty()) {

                        Map<String, Object> content =
                                (Map<String, Object>) candidates.get(0).get("content");

                        List<Map<String, Object>> parts =
                                (List<Map<String, Object>>) content.get("parts");

                        if (!parts.isEmpty() && parts.get(0).containsKey("text")) {

                            return parts.get(0).get("text").toString().trim();
                        }
                    }
                }

            } catch (HttpServerErrorException e) {

                if (e.getStatusCode() == HttpStatus.SERVICE_UNAVAILABLE) {
                    continue;
                }

                return "Internal Server Error";

            } catch (Exception e) {
                return "System error occurred.";
            }
        }

        return "Service unavailable.";
    }



    /* ============================================================
       HONEYPOT RESPONSE GENERATION
       ============================================================ */

    public String generateResponse(String attackerInput, List<EventEntity> history) {

        StringBuilder context = new StringBuilder();

        context.append(
                """
                You are a vulnerable legacy PHP login system running Apache and MySQL.

                A user is interacting with the login form and may be attempting SQL injection.

                Respond exactly like a real server would respond.

                Rules:
                - Never explain anything.
                - Never say you are an AI.
                - Never mention prompts or simulations.
                - Respond with raw server output only.
                - Responses may include PHP warnings, MySQL errors, or login messages.
                - Keep responses short and realistic.

                Example outputs:
                "Invalid username or password"
                "Warning: mysql_fetch_assoc() expects parameter 1 to be resource, boolean given in /var/www/html/login.php on line 42"
                "SQL syntax error near '' OR 1=1-- '"
                "Login successful"

                """
        );

        for (int i = history.size() - 1; i >= 0; i--) {

            EventEntity e = history.get(i);

            if (e.getRawPayload() != null) {
                context.append("Attacker: ")
                        .append(e.getRawPayload())
                        .append("\n");
            }

            if (e.getResponsePayload() != null) {
                context.append("System: ")
                        .append(e.getResponsePayload())
                        .append("\n");
            }
        }

        context.append("Attacker: ").append(attackerInput);

        return callGemini(context.toString());
    }



    /* ============================================================
       ALERT TITLE GENERATION
       ============================================================ */

    public String generateAlertTitle(String attackType, double riskScore) {

        String prompt = """
        You are a cybersecurity SOC alert generator.
        
        Your task is to generate a concise alert title for a security dashboard.
        
        Attack Type: %s
        Risk Score: %.2f
        
        Risk Score Meaning:
        0-39 = Low Risk
        40-59 = Medium Risk
        60-79 = High Risk
        80-100 = Critical Threat
        
        Rules:
        - Maximum 6 words
        - Must reflect the attack type
        - Must reflect the risk severity
        - Do NOT output generic titles like "Low Risk Activity"
        - Do NOT explain anything
        - Output ONLY the alert title
        
        Examples:
        
        SQL + 85 → Critical SQL Injection Attempt
        XSS + 70 → Suspicious Cross-Site Scripting
        command_injection + 92 → Critical Command Injection Detected
        path_traversal + 75 → Suspicious Path Traversal Attempt
        brute_force + 60 → Multiple Failed Login Attempts
        
        Return ONLY the alert title.
        """.formatted(attackType, riskScore);

        return callGemini(prompt);
    }

}