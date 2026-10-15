package com.dmitryefremov.site.telemetry.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.io.IOException;

@Component
public class TeleprompterIsolationInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws IOException {
        String requestUri = request.getRequestURI();
        if (requestUri == null) {
            return true;
        }

        String lowerUri = requestUri.toLowerCase();

        // Isolation: reject requests attempting to access raw teleprompter scripts or raw file paths
        if (lowerUri.contains("teleprompter") || lowerUri.contains("/mnt/hc_volume") || lowerUri.contains("raw-script") || lowerUri.contains("/podcast/episodes/raw")) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.setContentType("application/json");
            response.getWriter().write("{\"status\":403,\"message\":\"Access to raw teleprompter scripts and internal recording instruments is forbidden.\"}");
            return false;
        }

        return true;
    }
}
