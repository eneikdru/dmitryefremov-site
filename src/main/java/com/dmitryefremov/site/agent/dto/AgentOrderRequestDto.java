package com.dmitryefremov.site.agent.dto;

public record AgentOrderRequestDto(
        String serviceType,
        String clientName,
        String contactEmail,
        String requirements,
        String agentName
) {
}
