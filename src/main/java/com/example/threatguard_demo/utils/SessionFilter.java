package com.example.threatguard_demo.utils;

import com.example.threatguard_demo.models.entities.SessionEntity;
import com.example.threatguard_demo.service.sessions.SessionService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class SessionFilter extends OncePerRequestFilter {

    @Autowired
    private SessionService sessionService;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        String path = request.getRequestURI();

        // 🔥 Only track honeypot endpoints
        if (!path.startsWith("/login")) {
            filterChain.doFilter(request, response);
            return;
        }

        String userAgent = request.getHeader("User-Agent");

        String ip = request.getHeader("X-Forwarded-For");

        if (ip != null && !ip.isBlank()) {
            ip = ip.split(",")[0].trim();
        } else {
            ip = request.getRemoteAddr();
        }

        System.out.println("Resolved IP: " + ip);

        SessionEntity session =
                sessionService.getOrCreateSession(ip, userAgent);

        request.setAttribute("SESSION", session);

        filterChain.doFilter(request, response);
    }

}
