package com.dmitryefremov.site.telemetry.config;

import com.dmitryefremov.site.telemetry.interceptor.AgentTelemetryInterceptor;
import com.dmitryefremov.site.telemetry.interceptor.TeleprompterIsolationInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class TelemetryWebMvcConfig implements WebMvcConfigurer {

    private final AgentTelemetryInterceptor telemetryInterceptor;
    private final TeleprompterIsolationInterceptor teleprompterIsolationInterceptor;

    public TelemetryWebMvcConfig(AgentTelemetryInterceptor telemetryInterceptor,
                                 TeleprompterIsolationInterceptor teleprompterIsolationInterceptor) {
        this.telemetryInterceptor = telemetryInterceptor;
        this.teleprompterIsolationInterceptor = teleprompterIsolationInterceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(teleprompterIsolationInterceptor)
                .addPathPatterns("/**");
        registry.addInterceptor(telemetryInterceptor)
                .addPathPatterns("/api/v1/**");
    }
}
