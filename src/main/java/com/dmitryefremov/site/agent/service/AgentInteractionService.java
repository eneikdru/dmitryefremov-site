package com.dmitryefremov.site.agent.service;

import com.dmitryefremov.site.agent.domain.AgentInteractionTelemetry;
import com.dmitryefremov.site.agent.domain.AgentMessage;
import com.dmitryefremov.site.agent.domain.AgentOrder;
import com.dmitryefremov.site.agent.dto.*;
import com.dmitryefremov.site.agent.repository.AgentInteractionTelemetryRepository;
import com.dmitryefremov.site.agent.repository.AgentMessageRepository;
import com.dmitryefremov.site.agent.repository.AgentOrderRepository;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.OffsetDateTime;

@Service
public class AgentInteractionService {

    public static final String INTERACTION_ORDER = "ORDER";
    public static final String INTERACTION_MESSAGE = "MESSAGE";

    private final AgentOrderRepository orderRepository;
    private final AgentMessageRepository messageRepository;
    private final AgentInteractionTelemetryRepository telemetryRepository;
    private final MeterRegistry meterRegistry;
    private Clock clock = Clock.systemUTC();

    public AgentInteractionService(
            AgentOrderRepository orderRepository,
            AgentMessageRepository messageRepository,
            AgentInteractionTelemetryRepository telemetryRepository,
            @Autowired(required = false) MeterRegistry meterRegistry) {
        this.orderRepository = orderRepository;
        this.messageRepository = messageRepository;
        this.telemetryRepository = telemetryRepository;
        this.meterRegistry = meterRegistry;
    }

    public void setClock(Clock clock) {
        this.clock = clock;
    }

    @Transactional
    public AgentOrderResponse submitOrder(AgentOrderRequest request) {
        validateOrderRequest(request);

        OffsetDateTime now = OffsetDateTime.now(clock);
        AgentOrder order = new AgentOrder(
                request.getClientName().trim(),
                request.getClientEmail().trim(),
                request.getServiceType().trim(),
                request.getRequirements() != null ? request.getRequirements().trim() : "",
                "RECEIVED",
                now
        );

        AgentOrder saved = orderRepository.save(order);

        incrementTelemetryCounter(INTERACTION_ORDER, now);

        if (meterRegistry != null) {
            meterRegistry.counter("ai.agent.interactions", "type", "order").increment();
        }

        return new AgentOrderResponse(
                saved.getId(),
                saved.getClientName(),
                saved.getClientEmail(),
                saved.getServiceType(),
                saved.getRequirements(),
                saved.getStatus(),
                saved.getCreatedAt()
        );
    }

    @Transactional
    public AgentMessageResponse submitMessage(AgentMessageRequest request) {
        validateMessageRequest(request);

        OffsetDateTime now = OffsetDateTime.now(clock);
        AgentMessage message = new AgentMessage(
                request.getSenderName().trim(),
                request.getSenderEmail().trim(),
                request.getMessage().trim(),
                "SENT",
                now
        );

        AgentMessage saved = messageRepository.save(message);

        incrementTelemetryCounter(INTERACTION_MESSAGE, now);

        if (meterRegistry != null) {
            meterRegistry.counter("ai.agent.interactions", "type", "message").increment();
        }

        return new AgentMessageResponse(
                saved.getId(),
                saved.getSenderName(),
                saved.getSenderEmail(),
                saved.getMessage(),
                saved.getStatus(),
                saved.getCreatedAt()
        );
    }

    @Transactional(readOnly = true)
    public AgentTelemetryDto getTelemetry() {
        long totalOrders = telemetryRepository.findById(INTERACTION_ORDER)
                .map(AgentInteractionTelemetry::getCounter)
                .orElse(0L);

        long totalMessages = telemetryRepository.findById(INTERACTION_MESSAGE)
                .map(AgentInteractionTelemetry::getCounter)
                .orElse(0L);

        long totalInteractions = totalOrders + totalMessages;
        OffsetDateTime now = OffsetDateTime.now(clock);

        return new AgentTelemetryDto(totalOrders, totalMessages, totalInteractions, now);
    }

    private void incrementTelemetryCounter(String interactionType, OffsetDateTime now) {
        int rowsUpdated = telemetryRepository.incrementCounter(interactionType, now);
        if (rowsUpdated == 0) {
            AgentInteractionTelemetry telemetry = new AgentInteractionTelemetry(interactionType, 1L, now);
            telemetryRepository.save(telemetry);
        }
    }

    private void validateOrderRequest(AgentOrderRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("Order request cannot be null");
        }
        if (request.getClientName() == null || request.getClientName().isBlank()) {
            throw new IllegalArgumentException("Client name is required");
        }
        if (request.getClientEmail() == null || request.getClientEmail().isBlank()) {
            throw new IllegalArgumentException("Client email is required");
        }
        if (request.getServiceType() == null || request.getServiceType().isBlank()) {
            throw new IllegalArgumentException("Service type is required");
        }
    }

    private void validateMessageRequest(AgentMessageRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("Message request cannot be null");
        }
        if (request.getSenderName() == null || request.getSenderName().isBlank()) {
            throw new IllegalArgumentException("Sender name is required");
        }
        if (request.getSenderEmail() == null || request.getSenderEmail().isBlank()) {
            throw new IllegalArgumentException("Sender email is required");
        }
        if (request.getMessage() == null || request.getMessage().isBlank()) {
            throw new IllegalArgumentException("Message body is required");
        }
    }
}
