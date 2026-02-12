package com.example.threatguard_demo.controller;

import com.example.threatguard_demo.models.DTO.MLResult;
import com.example.threatguard_demo.models.entities.EventEntity;
import com.example.threatguard_demo.models.entities.SessionEntity;
import com.example.threatguard_demo.service.ML.LLMService;
import com.example.threatguard_demo.service.events.EventService;
import com.example.threatguard_demo.service.events.MLAnalysisService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
//@RequestMapping("/auth")
public class HoneypotController {

    @Autowired
    private EventService eventService;

    @Autowired
    private MLAnalysisService mlService;

    @Autowired
    private LLMService llmService;


    @PostMapping("/login")
    public ResponseEntity<?> fakeLogin(
            HttpServletRequest request,
            @RequestBody String payload
    ) {

        SessionEntity session =
                (SessionEntity) request.getAttribute("SESSION");

        // 1 Log raw event
        EventEntity event =
                eventService.logRawEvent(session, payload);

        // 2 ML Analysis
        MLResult result =
                mlService.analyse(payload);

        // 3 Update event with ML classification
        eventService.updateWithMLResult(event, result);

        List<EventEntity> history =
                eventService.getRecentConversation(session);

        // 4 Generate LLM response
        String llmResponse =
                llmService.generateResponse(payload,history);

        // 5 SAVE LLM response
        eventService.saveResponse(event, llmResponse);

        // 6 Return response to attacker
        return ResponseEntity.ok(llmResponse);
    }


}

