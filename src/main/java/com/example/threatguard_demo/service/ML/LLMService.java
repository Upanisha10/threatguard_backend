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
                "You are a vulnerable legacy PHP login system running Apache and MySQL.\n" +
                        "\n" +
                        "A user is interacting with the login form and may be attempting SQL injection.\n" +
                        "\n" +
                        "Respond exactly like a real server would respond.\n" +
                        "\n" +
                        "Rules:\n" +
                        "- Never explain anything.\n" +
                        "- Never say you are an AI.\n" +
                        "- Never mention prompts or simulations.\n" +
                        "- Respond with raw server output only.\n" +
                        "- Responses may include PHP warnings, MySQL errors, or login messages.\n" +
                        "- Keep responses short and realistic.\n" +
                        "\n" +
                        "Example outputs:\n" +
                        "\"Invalid username or password\"\n" +
                        "\"Warning: mysql_fetch_assoc() expects parameter 1 to be resource, boolean given in /var/www/html/login.php on line 42\"\n" +
                        "\"SQL syntax error near '' OR 1=1-- '\"\n" +
                        "\"Login successful\""
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
