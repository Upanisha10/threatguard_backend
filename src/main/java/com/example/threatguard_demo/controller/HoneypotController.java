package com.example.threatguard_demo.controller;

import com.example.threatguard_demo.models.DTO.MLResult;
import com.example.threatguard_demo.models.entities.EventEntity;
import com.example.threatguard_demo.models.entities.SessionEntity;
import com.example.threatguard_demo.service.ML.LLMService;
import com.example.threatguard_demo.service.events.EventService;
import com.example.threatguard_demo.service.events.MLAnalysisService;
import com.example.threatguard_demo.service.sessions.SessionService;
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

    @Autowired
    private SessionService sessionService;


    @PostMapping("/login")
    public ResponseEntity<?> fakeLogin(
            HttpServletRequest request,
            @RequestBody String payload
    ) {

        String ip = request.getHeader("X-Forwarded-For");

        if (ip != null && !ip.isBlank()) {
            ip = ip.split(",")[0].trim();
        } else {
            ip = request.getRemoteAddr();
        }
        if (ip.equals("127.0.0.1") || ip.equals("::1") || ip.equals("0:0:0:0:0:0:0:1")) {
            ip = "127.0.0.1";
        }

        String ua = request.getHeader("User-Agent");

        SessionEntity session = sessionService.getOrCreateSession(ip, ua);

        EventEntity event = eventService.logRawEvent(session, payload);

        MLResult result = mlService.analyse(payload);

        eventService.updateWithMLResult(event, result);

        List<EventEntity> history = eventService.getRecentConversation(session);

        String llmResponse = llmService.generateResponse(payload, history);

        eventService.saveResponse(event, llmResponse);

        return ResponseEntity.ok(llmResponse);
    }


}

