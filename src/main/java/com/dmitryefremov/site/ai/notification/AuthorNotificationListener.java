package com.dmitryefremov.site.ai.notification;

import com.dmitryefremov.site.ai.domain.AiDirectMessage;
import com.dmitryefremov.site.ai.domain.AiServiceOrder;
import com.dmitryefremov.site.ai.event.AiDirectMessageCreatedEvent;
import com.dmitryefremov.site.ai.event.AiServiceOrderCreatedEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
public class AuthorNotificationListener {

    private final AuthorNotificationService authorNotificationService;

    @Autowired
    public AuthorNotificationListener(@Qualifier("siteAuthorNotificationService") AuthorNotificationService authorNotificationService) {
        this.authorNotificationService = authorNotificationService;
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT, fallbackExecution = true)
    public void onServiceOrderCreated(AiServiceOrderCreatedEvent event) {
        if (event == null || event.getServiceOrder() == null) {
            return;
        }

        AiServiceOrder order = event.getServiceOrder();
        String content = "Service Order (" + order.getServiceType() + "): " +
                (order.getRequirements() != null ? order.getRequirements() : "");

        authorNotificationService.dispatchNotification(
                "SERVICE_ORDER",
                order.getUserName(),
                order.getUserEmail(),
                order.getAgentIdentifier(),
                content
        );
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT, fallbackExecution = true)
    public void onDirectMessageCreated(AiDirectMessageCreatedEvent event) {
        if (event == null || event.getDirectMessage() == null) {
            return;
        }

        AiDirectMessage message = event.getDirectMessage();
        authorNotificationService.dispatchNotification(
                "DIRECT_MESSAGE",
                message.getSenderName(),
                message.getSenderEmail(),
                message.getAgentIdentifier(),
                message.getContent()
        );
    }
}
