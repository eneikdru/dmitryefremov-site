package com.dmitryefremov.site.ai.service;

import com.dmitryefremov.site.ai.domain.AiDirectMessage;
import com.dmitryefremov.site.ai.domain.AiServiceOrder;
import com.dmitryefremov.site.ai.event.AiDirectMessageCreatedEvent;
import com.dmitryefremov.site.ai.event.AiServiceOrderCreatedEvent;
import com.dmitryefremov.site.ai.repository.AiDirectMessageRepository;
import com.dmitryefremov.site.ai.repository.AiServiceOrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.OffsetDateTime;

@Service("siteAiSubmissionService")
public class AiSubmissionService {

    private final AiServiceOrderRepository aiServiceOrderRepository;
    private final AiDirectMessageRepository aiDirectMessageRepository;
    private final ApplicationEventPublisher eventPublisher;
    private final Clock clock;

    @Autowired
    public AiSubmissionService(AiServiceOrderRepository aiServiceOrderRepository,
                               AiDirectMessageRepository aiDirectMessageRepository,
                               ApplicationEventPublisher eventPublisher,
                               @Autowired(required = false) Clock clock) {
        this.aiServiceOrderRepository = aiServiceOrderRepository;
        this.aiDirectMessageRepository = aiDirectMessageRepository;
        this.eventPublisher = eventPublisher;
        this.clock = clock != null ? clock : Clock.systemUTC();
    }

    public AiSubmissionService(AiServiceOrderRepository aiServiceOrderRepository,
                               AiDirectMessageRepository aiDirectMessageRepository,
                               ApplicationEventPublisher eventPublisher) {
        this(aiServiceOrderRepository, aiDirectMessageRepository, eventPublisher, Clock.systemUTC());
    }

    public Clock getClock() {
        return clock;
    }

    @Transactional
    public AiServiceOrder createServiceOrder(String userName,
                                             String userEmail,
                                             String agentIdentifier,
                                             String serviceType,
                                             String requirements) {
        if (serviceType == null || serviceType.isBlank()) {
            throw new IllegalArgumentException("Service type cannot be null or empty");
        }

        OffsetDateTime now = OffsetDateTime.now(clock);
        AiServiceOrder order = new AiServiceOrder(
                userName,
                userEmail,
                agentIdentifier,
                serviceType,
                requirements,
                now
        );

        AiServiceOrder saved = aiServiceOrderRepository.save(order);
        if (eventPublisher != null) {
            eventPublisher.publishEvent(new AiServiceOrderCreatedEvent(saved));
        }
        return saved;
    }

    @Transactional
    public AiDirectMessage createDirectMessage(String senderName,
                                               String senderEmail,
                                               String agentIdentifier,
                                               String content) {
        if (content == null || content.isBlank()) {
            throw new IllegalArgumentException("Message content cannot be null or empty");
        }

        OffsetDateTime now = OffsetDateTime.now(clock);
        AiDirectMessage message = new AiDirectMessage(
                senderName,
                senderEmail,
                agentIdentifier,
                content,
                now
        );

        AiDirectMessage saved = aiDirectMessageRepository.save(message);
        if (eventPublisher != null) {
            eventPublisher.publishEvent(new AiDirectMessageCreatedEvent(saved));
        }
        return saved;
    }
}
