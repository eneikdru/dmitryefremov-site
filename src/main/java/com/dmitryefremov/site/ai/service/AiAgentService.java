package com.dmitryefremov.site.ai.service;

import com.dmitryefremov.site.ai.domain.AiDirectMessage;
import com.dmitryefremov.site.ai.domain.AiServiceOrder;
import com.dmitryefremov.site.ai.dto.AiDirectMessageRequestDto;
import com.dmitryefremov.site.ai.dto.AiDirectMessageResponseDto;
import com.dmitryefremov.site.ai.dto.AiServiceOrderRequestDto;
import com.dmitryefremov.site.ai.dto.AiServiceOrderResponseDto;
import com.dmitryefremov.site.ai.repository.AiDirectMessageRepository;
import com.dmitryefremov.site.ai.repository.AiServiceOrderRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.OffsetDateTime;

@Service
public class AiAgentService {

    private final AiServiceOrderRepository orderRepository;
    private final AiDirectMessageRepository messageRepository;
    private final Clock clock;

    public AiAgentService(AiServiceOrderRepository orderRepository,
                          AiDirectMessageRepository messageRepository,
                          Clock clock) {
        this.orderRepository = orderRepository;
        this.messageRepository = messageRepository;
        this.clock = clock;
    }

    @Transactional
    public AiServiceOrderResponseDto createOrder(AiServiceOrderRequestDto dto) {
        OffsetDateTime now = OffsetDateTime.now(clock);
        AiServiceOrder order = new AiServiceOrder(
                dto.getUserName(),
                dto.getUserEmail(),
                dto.getAgentIdentifier(),
                dto.getServiceType(),
                dto.getRequirements(),
                now
        );

        AiServiceOrder saved = orderRepository.save(order);
        String ackMessage = "Service order for '" + saved.getServiceType() + "' received successfully. We will follow up shortly via " + saved.getUserEmail();

        return new AiServiceOrderResponseDto(
                saved.getId(),
                saved.getStatus(),
                ackMessage,
                now
        );
    }

    @Transactional
    public AiDirectMessageResponseDto createMessage(AiDirectMessageRequestDto dto) {
        OffsetDateTime now = OffsetDateTime.now(clock);
        AiDirectMessage message = new AiDirectMessage(
                dto.getSenderName(),
                dto.getSenderEmail(),
                dto.getAgentIdentifier(),
                dto.getContent(),
                now
        );

        AiDirectMessage saved = messageRepository.save(message);
        String ackMessage = "Direct message received successfully. Thank you for reaching out!";

        return new AiDirectMessageResponseDto(
                saved.getId(),
                saved.getStatus(),
                ackMessage,
                now
        );
    }
}
