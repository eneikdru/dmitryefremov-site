package com.dmitryefremov.site.agent.dto;

import java.time.OffsetDateTime;

public record AgentTelemetryDto(
        long totalOrders,
        long totalMessages,
        long totalInteractions,
        OffsetDateTime timestamp
) {
}
