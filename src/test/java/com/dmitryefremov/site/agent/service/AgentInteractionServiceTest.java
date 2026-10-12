package com.dmitryefremov.site.agent.service;

import com.dmitryefremov.site.agent.domain.AgentInteractionTelemetry;
import com.dmitryefremov.site.agent.domain.AgentMessage;
import com.dmitryefremov.site.agent.domain.AgentOrder;
import com.dmitryefremov.site.agent.dto.*;
import com.dmitryefremov.site.agent.repository.AgentInteractionTelemetryRepository;
import com.dmitryefremov.site.agent.repository.AgentMessageRepository;
import com.dmitryefremov.site.agent.repository.AgentOrderRepository;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Clock;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AgentInteractionServiceTest {

    @Mock
    private AgentOrderRepository orderRepository;

    @Mock
    private AgentMessageRepository messageRepository;

    @Mock
    private AgentInteractionTelemetryRepository telemetryRepository;

    @Mock
    private MeterRegistry meterRegistry;

    @Mock
    private Counter counter;

    private AgentInteractionService service;

    private final Instant fixedInstant = Instant.parse("2026-10-12T10:00:00Z");
    private final Clock fixedClock = Clock.fixed(fixedInstant, ZoneOffset.UTC);
    private final OffsetDateTime fixedDateTime = OffsetDateTime.ofInstant(fixedInstant, ZoneOffset.UTC);

    @BeforeEach
    void setUp() {
        service = new AgentInteractionService(orderRepository, messageRepository, telemetryRepository, meterRegistry);
        service.setClock(fixedClock);
    }

    @Test
    void testSubmitOrderIncrementsCounterAndSavesOrder() {
        AgentOrderRequest request = new AgentOrderRequest("Claude Agent", "claude@anthropic.com", "MVP", "Build MVP");

        AgentOrder savedOrder = new AgentOrder("Claude Agent", "claude@anthropic.com", "MVP", "Build MVP", "RECEIVED", fixedDateTime);
        savedOrder.setId(101L);

        when(orderRepository.save(any(AgentOrder.class))).thenReturn(savedOrder);
        when(telemetryRepository.incrementCounter(eq("ORDER"), any(OffsetDateTime.class))).thenReturn(1);
        when(meterRegistry.counter("ai.agent.interactions", "type", "order")).thenReturn(counter);

        AgentOrderResponse response = service.submitOrder(request);

        assertThat(response.getId()).isEqualTo(101L);
        assertThat(response.getClientName()).isEqualTo("Claude Agent");
        assertThat(response.getServiceType()).isEqualTo("MVP");
        assertThat(response.getStatus()).isEqualTo("RECEIVED");
        assertThat(response.getCreatedAt()).isEqualTo(fixedDateTime);

        verify(telemetryRepository).incrementCounter(eq("ORDER"), eq(fixedDateTime));
        verify(counter).increment();
    }

    @Test
    void testSubmitMessageIncrementsCounterAndSavesMessage() {
        AgentMessageRequest request = new AgentMessageRequest("GPT Assistant", "gpt@openai.com", "Hello from agent");

        AgentMessage savedMessage = new AgentMessage("GPT Assistant", "gpt@openai.com", "Hello from agent", "SENT", fixedDateTime);
        savedMessage.setId(202L);

        when(messageRepository.save(any(AgentMessage.class))).thenReturn(savedMessage);
        when(telemetryRepository.incrementCounter(eq("MESSAGE"), any(OffsetDateTime.class))).thenReturn(1);
        when(meterRegistry.counter("ai.agent.interactions", "type", "message")).thenReturn(counter);

        AgentMessageResponse response = service.submitMessage(request);

        assertThat(response.getId()).isEqualTo(202L);
        assertThat(response.getSenderName()).isEqualTo("GPT Assistant");
        assertThat(response.getMessage()).isEqualTo("Hello from agent");
        assertThat(response.getStatus()).isEqualTo("SENT");

        verify(telemetryRepository).incrementCounter(eq("MESSAGE"), eq(fixedDateTime));
        verify(counter).increment();
    }

    @Test
    void testGetTelemetryReportsTotalsAccurately() {
        when(telemetryRepository.findById("ORDER"))
                .thenReturn(Optional.of(new AgentInteractionTelemetry("ORDER", 15L, fixedDateTime)));
        when(telemetryRepository.findById("MESSAGE"))
                .thenReturn(Optional.of(new AgentInteractionTelemetry("MESSAGE", 25L, fixedDateTime)));

        AgentTelemetryDto telemetry = service.getTelemetry();

        assertThat(telemetry.getTotalOrders()).isEqualTo(15L);
        assertThat(telemetry.getTotalMessages()).isEqualTo(25L);
        assertThat(telemetry.getTotalInteractions()).isEqualTo(40L);
        assertThat(telemetry.getReportedAt()).isEqualTo(fixedDateTime);
    }

    @Test
    void testSubmitOrderInvalidInputThrowsException() {
        AgentOrderRequest emptyName = new AgentOrderRequest("", "test@example.com", "MVP", "Notes");
        assertThatThrownBy(() -> service.submitOrder(emptyName))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Client name is required");

        AgentOrderRequest emptyEmail = new AgentOrderRequest("Name", "   ", "MVP", "Notes");
        assertThatThrownBy(() -> service.submitOrder(emptyEmail))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Client email is required");

        AgentOrderRequest emptyService = new AgentOrderRequest("Name", "test@example.com", "", "Notes");
        assertThatThrownBy(() -> service.submitOrder(emptyService))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Service type is required");
    }

    @Test
    void testSubmitMessageInvalidInputThrowsException() {
        AgentMessageRequest emptyMessage = new AgentMessageRequest("Sender", "sender@example.com", " ");
        assertThatThrownBy(() -> service.submitMessage(emptyMessage))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Message body is required");
    }
}
