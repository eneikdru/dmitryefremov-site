package com.dmitryefremov.site.ai.notification;

import com.dmitryefremov.site.ai.domain.AiDirectMessage;
import com.dmitryefremov.site.ai.domain.AiServiceOrder;
import com.dmitryefremov.site.ai.event.AiDirectMessageCreatedEvent;
import com.dmitryefremov.site.ai.event.AiServiceOrderCreatedEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthorNotificationListenerTest {

    @Mock
    private AuthorNotificationService authorNotificationService;

    private AuthorNotificationListener listener;

    @BeforeEach
    void setUp() {
        listener = new AuthorNotificationListener(authorNotificationService);
    }

    @Test
    void onServiceOrderCreated_dispatchesNotificationWithSenderDetailsAndContent() {
        Clock fixedClock = Clock.fixed(Instant.parse("2026-10-12T10:00:00Z"), ZoneOffset.UTC);
        AiServiceOrder order = new AiServiceOrder(
                "Alice",
                "alice@example.com",
                "agent-1",
                "Audit",
                "Security audit for backend API",
                fixedClock
        );

        listener.onServiceOrderCreated(new AiServiceOrderCreatedEvent(order));

        verify(authorNotificationService).dispatchNotification(
                "SERVICE_ORDER",
                "Alice",
                "alice@example.com",
                "agent-1",
                "Service Order (Audit): Security audit for backend API"
        );
    }

    @Test
    void onDirectMessageCreated_dispatchesNotificationWithSenderDetailsAndContent() {
        Clock fixedClock = Clock.fixed(Instant.parse("2026-10-12T10:00:00Z"), ZoneOffset.UTC);
        AiDirectMessage message = new AiDirectMessage(
                "Bob",
                "bob@example.com",
                "agent-2",
                "Hello author, I would like to consult on architecture.",
                fixedClock
        );

        listener.onDirectMessageCreated(new AiDirectMessageCreatedEvent(message));

        verify(authorNotificationService).dispatchNotification(
                "DIRECT_MESSAGE",
                "Bob",
                "bob@example.com",
                "agent-2",
                "Hello author, I would like to consult on architecture."
        );
    }

    @Test
    void handlesNullEventGracefully() {
        listener.onServiceOrderCreated(null);
        listener.onDirectMessageCreated(null);

        verifyNoInteractions(authorNotificationService);
    }
}
