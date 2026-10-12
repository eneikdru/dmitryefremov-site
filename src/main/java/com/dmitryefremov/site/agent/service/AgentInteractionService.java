package com.dmitryefremov.site.agent.service;

import com.dmitryefremov.site.agent.domain.AgentMessage;
import com.dmitryefremov.site.agent.domain.AgentOrder;
import com.dmitryefremov.site.agent.dto.*;
import com.dmitryefremov.site.agent.repository.AgentMessageRepository;
import com.dmitryefremov.site.agent.repository.AgentOrderRepository;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;

@Service
public class AgentInteractionService {

    private final AgentOrderRepository agentOrderRepository;
    private final AgentMessageRepository agentMessageRepository;
    private final Counter orderCounter;
    private final Counter messageCounter;

    public AgentInteractionService(AgentOrderRepository agentOrderRepository,
                                   AgentMessageRepository agentMessageRepository,
                                   MeterRegistry meterRegistry) {
        this.agentOrderRepository = agentOrderRepository;
        this.agentMessageRepository = agentMessageRepository;
        this.orderCounter = Counter.builder("ai.agent.interactions")
                .tag("type", "order")
                .description("Counter of AI agent service orders")
                .register(meterRegistry);
        this.messageCounter = Counter.builder("ai.agent.interactions")
                .tag("type", "message")
                .description("Counter of AI agent messages")
                .register(meterRegistry);
    }

    @Transactional
    public AgentOrderResponseDto submitOrder(AgentOrderRequestDto request) {
        if (request.serviceType() == null || request.serviceType().isBlank()) {
            throw new IllegalArgumentException("serviceType must not be blank");
        }
        if (request.clientName() == null || request.clientName().isBlank()) {
            throw new IllegalArgumentException("clientName must not be blank");
        }
        if (request.contactEmail() == null || request.contactEmail().isBlank()) {
            throw new IllegalArgumentException("contactEmail must not be blank");
        }

        AgentOrder order = new AgentOrder(
                request.serviceType(),
                request.clientName(),
                request.contactEmail(),
                request.requirements(),
                request.agentName() != null ? request.agentName() : "UnknownAgent"
        );
        AgentOrder saved = agentOrderRepository.save(order);
        orderCounter.increment();

        return new AgentOrderResponseDto(
                saved.getId(),
                saved.getServiceType(),
                saved.getClientName(),
                saved.getContactEmail(),
                saved.getRequirements(),
                saved.getAgentName(),
                saved.getCreatedAt()
        );
    }

    @Transactional
    public AgentMessageResponseDto sendMessage(AgentMessageRequestDto request) {
        if (request.senderName() == null || request.senderName().isBlank()) {
            throw new IllegalArgumentException("senderName must not be blank");
        }
        if (request.contactEmail() == null || request.contactEmail().isBlank()) {
            throw new IllegalArgumentException("contactEmail must not be blank");
        }
        if (request.messageText() == null || request.messageText().isBlank()) {
            throw new IllegalArgumentException("messageText must not be blank");
        }

        AgentMessage message = new AgentMessage(
                request.senderName(),
                request.contactEmail(),
                request.messageText(),
                request.agentName() != null ? request.agentName() : "UnknownAgent"
        );
        AgentMessage saved = agentMessageRepository.save(message);
        messageCounter.increment();

        return new AgentMessageResponseDto(
                saved.getId(),
                saved.getSenderName(),
                saved.getContactEmail(),
                saved.getMessageText(),
                saved.getAgentName(),
                saved.getCreatedAt()
        );
    }

    @Transactional(readOnly = true)
    public AgentTelemetryDto getTelemetry() {
        long totalOrders = agentOrderRepository.count();
        long totalMessages = agentMessageRepository.count();
        return new AgentTelemetryDto(
                totalOrders,
                totalMessages,
                totalOrders + totalMessages,
                OffsetDateTime.now()
        );
    }
}
