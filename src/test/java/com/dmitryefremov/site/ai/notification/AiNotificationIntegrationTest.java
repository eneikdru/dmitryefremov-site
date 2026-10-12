package com.dmitryefremov.site.ai.notification;

import com.dmitryefremov.site.ai.domain.AiDirectMessage;
import com.dmitryefremov.site.ai.domain.AiServiceOrder;
import com.dmitryefremov.site.ai.service.AiSubmissionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class AiNotificationIntegrationTest {

    @Autowired
    private AiSubmissionService aiSubmissionService;

    @Autowired
    private AuthorNotificationService authorNotificationService;

    @Autowired
    private PlatformTransactionManager transactionManager;

    private TransactionTemplate transactionTemplate;

    @BeforeEach
    void setUp() {
        authorNotificationService.clearDispatchedNotifications();
        transactionTemplate = new TransactionTemplate(transactionManager);
    }

    @Test
    void whenServiceOrderCreatedAndTransactionCommits_dispatchesNotificationToAuthorWithSenderDetailsAndContent() {
        AiServiceOrder createdOrder = transactionTemplate.execute(status ->
                aiSubmissionService.createServiceOrder(
                        "Eve User",
                        "eve@example.com",
                        "chatgpt-plus",
                        "MVP",
                        "Create MVP for customer portal"
                )
        );

        assertThat(createdOrder).isNotNull();
        assertThat(createdOrder.getId()).isNotNull();

        List<AuthorNotification> notifications = authorNotificationService.getDispatchedNotifications();
        assertThat(notifications).hasSize(1);

        AuthorNotification notification = notifications.get(0);
        assertThat(notification.getRecipientEmail()).isEqualTo("author@dmitryefremov.com");
        assertThat(notification.getSubmissionType()).isEqualTo("SERVICE_ORDER");
        assertThat(notification.getSenderName()).isEqualTo("Eve User");
        assertThat(notification.getSenderEmail()).isEqualTo("eve@example.com");
        assertThat(notification.getAgentIdentifier()).isEqualTo("chatgpt-plus");
        assertThat(notification.getContent()).contains("MVP", "Create MVP for customer portal");
    }

    @Test
    void whenDirectMessageCreatedAndTransactionCommits_dispatchesNotificationToAuthorWithSenderDetailsAndContent() {
        AiDirectMessage createdMessage = transactionTemplate.execute(status ->
                aiSubmissionService.createDirectMessage(
                        "Frank Assistant",
                        "frank@example.com",
                        "claude-pro",
                        "Hello author, we would like to discuss consulting."
                )
        );

        assertThat(createdMessage).isNotNull();
        assertThat(createdMessage.getId()).isNotNull();

        List<AuthorNotification> notifications = authorNotificationService.getDispatchedNotifications();
        assertThat(notifications).hasSize(1);

        AuthorNotification notification = notifications.get(0);
        assertThat(notification.getRecipientEmail()).isEqualTo("author@dmitryefremov.com");
        assertThat(notification.getSubmissionType()).isEqualTo("DIRECT_MESSAGE");
        assertThat(notification.getSenderName()).isEqualTo("Frank Assistant");
        assertThat(notification.getSenderEmail()).isEqualTo("frank@example.com");
        assertThat(notification.getAgentIdentifier()).isEqualTo("claude-pro");
        assertThat(notification.getContent()).isEqualTo("Hello author, we would like to discuss consulting.");
    }
}
