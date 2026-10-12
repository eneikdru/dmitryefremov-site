package com.dmitryefremov.site.ai.service;

import com.dmitryefremov.site.ai.domain.AiDirectMessage;
import com.dmitryefremov.site.ai.domain.AiServiceOrder;
import com.dmitryefremov.site.ai.event.AiDirectMessageCreatedEvent;
import com.dmitryefremov.site.ai.event.AiServiceOrderCreatedEvent;
import com.dmitryefremov.site.ai.repository.AiDirectMessageRepository;
import com.dmitryefremov.site.ai.repository.AiServiceOrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AiSubmissionServiceTest {

    @Mock
    private AiServiceOrderRepository aiServiceOrderRepository;

    @Mock
    private AiDirectMessageRepository aiDirectMessageRepository;

    @Mock
    private ApplicationEventPublisher eventPublisher;

    private Clock fixedClock;
    private AiSubmissionService aiSubmissionService;

    @BeforeEach
    void setUp() {
        fixedClock = Clock.fixed(Instant.parse("2026-10-12T10:00:00Z"), ZoneOffset.UTC);
        aiSubmissionService = new AiSubmissionService(
                aiServiceOrderRepository,
                aiDirectMessageRepository,
                eventPublisher,
                fixedClock
        );
    }

    @Test
    void createServiceOrder_savesOrderAndPublishesEvent() {
        when(aiServiceOrderRepository.save(any(AiServiceOrder.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        AiServiceOrder result = aiSubmissionService.createServiceOrder(
                "Alice",
                "alice@example.com",
                "gpt-4",
                "MVP",
                "Build fullstack web app"
        );

        assertThat(result.getUserName()).isEqualTo("Alice");
        assertThat(result.getUserEmail()).isEqualTo("alice@example.com");
        assertThat(result.getAgentIdentifier()).isEqualTo("gpt-4");
        assertThat(result.getServiceType()).isEqualTo("MVP");
        assertThat(result.getRequirements()).isEqualTo("Build fullstack web app");
        assertThat(result.getCreatedAt()).isEqualTo("2026-10-12T10:00:00Z");

        ArgumentCaptor<AiServiceOrderCreatedEvent> eventCaptor = ArgumentCaptor.forClass(AiServiceOrderCreatedEvent.class);
        verify(eventPublisher).publishEvent(eventCaptor.capture());
        assertThat(eventCaptor.getValue().getServiceOrder()).isEqualTo(result);
    }

    @Test
    void createServiceOrder_throwsExceptionWhenServiceTypeBlank() {
        assertThatThrownBy(() -> aiSubmissionService.createServiceOrder("Alice", "alice@example.com", "agent", "", "reqs"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Service type cannot be null or empty");

        verifyNoInteractions(aiServiceOrderRepository, eventPublisher);
    }

    @Test
    void createDirectMessage_savesMessageAndPublishesEvent() {
        when(aiDirectMessageRepository.save(any(AiDirectMessage.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        AiDirectMessage result = aiSubmissionService.createDirectMessage(
                "Bob",
                "bob@example.com",
                "claude-3-5",
                "Inquiry about audit options"
        );

        assertThat(result.getSenderName()).isEqualTo("Bob");
        assertThat(result.getSenderEmail()).isEqualTo("bob@example.com");
        assertThat(result.getAgentIdentifier()).isEqualTo("claude-3-5");
        assertThat(result.getContent()).isEqualTo("Inquiry about audit options");
        assertThat(result.getCreatedAt()).isEqualTo("2026-10-12T10:00:00Z");

        ArgumentCaptor<AiDirectMessageCreatedEvent> eventCaptor = ArgumentCaptor.forClass(AiDirectMessageCreatedEvent.class);
        verify(eventPublisher).publishEvent(eventCaptor.capture());
        assertThat(eventCaptor.getValue().getDirectMessage()).isEqualTo(result);
    }

    @Test
    void createDirectMessage_throwsExceptionWhenContentBlank() {
        assertThatThrownBy(() -> aiSubmissionService.createDirectMessage("Bob", "bob@example.com", "agent", "   "))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Message content cannot be null or empty");

        verifyNoInteractions(aiDirectMessageRepository, eventPublisher);
    }
}
