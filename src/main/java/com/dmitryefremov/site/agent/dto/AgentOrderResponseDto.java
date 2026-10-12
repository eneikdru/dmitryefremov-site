package com.dmitryefremov.site.agent.dto;

import java.time.OffsetDateTime;

public record AgentOrderResponseDto(
        Long id,
        String serviceType,
        String clientName,
        String contactEmail,
        String requirements,
        String agentName,
        OffsetDateTime createdAt
) {
}
