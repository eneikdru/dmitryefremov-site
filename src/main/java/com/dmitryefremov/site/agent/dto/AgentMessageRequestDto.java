package com.dmitryefremov.site.agent.dto;

public record AgentMessageRequestDto(
        String senderName,
        String contactEmail,
        String messageText,
        String agentName
) {
}
