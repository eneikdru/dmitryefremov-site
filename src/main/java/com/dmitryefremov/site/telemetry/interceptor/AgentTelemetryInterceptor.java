package com.dmitryefremov.site.telemetry.interceptor;

import com.dmitryefremov.site.telemetry.service.AgentTelemetryService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.Set;

@Component
public class AgentTelemetryInterceptor implements HandlerInterceptor {

    private static final Set<String> UPDATE_METHODS = Set.of("POST", "PUT", "PATCH", "DELETE");

    @Autowired(required = false)
    private AgentTelemetryService telemetryService;

    public AgentTelemetryInterceptor() {
    }

    public AgentTelemetryInterceptor(AgentTelemetryService telemetryService) {
        this.telemetryService = telemetryService;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        if (telemetryService == null) {
            return true;
        }

        String uri = request.getRequestURI();
        // Do not track calls to telemetry metrics endpoint itself to prevent skewing
        if (uri != null && uri.contains("/api/v1/telemetry")) {
            return true;
        }

        telemetryService.incrementApiRequests();

        if (UPDATE_METHODS.contains(request.getMethod().toUpperCase())) {
            telemetryService.incrementAgentUpdates();
        }

        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        if (telemetryService == null) {
            return;
        }

        String uri = request.getRequestURI();
        if (uri != null && uri.contains("/api/v1/telemetry")) {
            return;
        }

        int status = response.getStatus();
        if (status >= 400 || ex != null) {
            telemetryService.incrementFailedApiRequests();
        }
    }
}
