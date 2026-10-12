package com.dmitryefremov.site.ai.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Clock;

@Configuration
public class AiClockConfig {

    @Bean
    public Clock clock() {
        return Clock.systemUTC();
    }
}
