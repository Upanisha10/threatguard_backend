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

    private static final String BASE_URL = "https://generativelanguage.googleapis.com/v1beta/";

    public String generateResponse(String attackerInput, List<EventEntity> history) {

        // Build Stateful Conversation Context
        StringBuilder context = new StringBuilder();

        context.append(
                "You are a vulnerable legacy web login system interacting with a potential attacker.\n" +
                        "Respond realistically like a real server.\n" +
                        "Never reveal you are AI.\n" +
                        "Keep responses short and believable.\n\n"
        );

        // Add previous conversation history
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

        // Add current attacker input
        context.append("Attacker: ").append(attackerInput);

        RestTemplate restTemplate = new RestTemplate();

        Map<String, Object> requestBody = Map.of(
                "contents", List.of(
                        Map.of("parts", List.of(
                                Map.of("text", context.toString())
                        ))
                )
        );

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

        for (String model : MODELS) {

            String url = BASE_URL + model + ":generateContent?key=" + apiKey;

            try {

                ResponseEntity<Map> response =
                        restTemplate.postForEntity(url, entity, Map.class);

                if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {

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
}
