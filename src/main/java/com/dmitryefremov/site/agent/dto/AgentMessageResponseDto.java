package com.dmitryefremov.site.agent.dto;

import java.time.OffsetDateTime;

public record AgentMessageResponseDto(
        Long id,
        String senderName,
        String contactEmail,
        String messageText,
        String agentName,
        OffsetDateTime createdAt
) {
}
