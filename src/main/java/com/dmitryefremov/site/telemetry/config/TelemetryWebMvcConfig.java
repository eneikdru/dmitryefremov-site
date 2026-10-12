package com.dmitryefremov.site.telemetry.config;

import com.dmitryefremov.site.telemetry.interceptor.AgentTelemetryInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class TelemetryWebMvcConfig implements WebMvcConfigurer {

    private final AgentTelemetryInterceptor telemetryInterceptor;

    public TelemetryWebMvcConfig(AgentTelemetryInterceptor telemetryInterceptor) {
        this.telemetryInterceptor = telemetryInterceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(telemetryInterceptor)
                .addPathPatterns("/api/v1/**");
    }
}
