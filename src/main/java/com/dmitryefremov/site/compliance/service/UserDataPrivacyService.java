package com.dmitryefremov.site.compliance.service;

import com.dmitryefremov.site.compliance.domain.AgentMessage;
import com.dmitryefremov.site.compliance.domain.AgentOrder;
import com.dmitryefremov.site.compliance.dto.AgentMessageDto;
import com.dmitryefremov.site.compliance.dto.AgentOrderDto;
import com.dmitryefremov.site.compliance.dto.ErasureResultDto;
import com.dmitryefremov.site.compliance.dto.UserDataExportDto;
import com.dmitryefremov.site.compliance.repository.AgentMessageRepository;
import com.dmitryefremov.site.compliance.repository.AgentOrderRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;

import java.util.List;

@Service
public class UserDataPrivacyService {

    private final AgentOrderRepository orderRepository;
    private final AgentMessageRepository messageRepository;
    private final Clock clock;

    public UserDataPrivacyService(AgentOrderRepository orderRepository,
                                  AgentMessageRepository messageRepository,
                                  Clock clock) {
        this.orderRepository = orderRepository;
        this.messageRepository = messageRepository;
        this.clock = clock;
    }

    @Transactional(readOnly = true)
    public UserDataExportDto exportUserData(String userIdentifier) {
        if (userIdentifier == null || userIdentifier.trim().isEmpty()) {
            throw new IllegalArgumentException("userIdentifier must not be empty");
        }

        List<AgentOrderDto> orderDtos = orderRepository.findByUserIdentifier(userIdentifier)
                .stream()
                .map(this::mapOrderToDto)
                .toList();

        List<AgentMessageDto> messageDtos = messageRepository.findByUserIdentifier(userIdentifier)
                .stream()
                .map(this::mapMessageToDto)
                .toList();

        return new UserDataExportDto(userIdentifier, orderDtos, messageDtos);
    }

    @Transactional
    public ErasureResultDto eraseUserData(String userIdentifier) {
        if (userIdentifier == null || userIdentifier.trim().isEmpty()) {
            throw new IllegalArgumentException("userIdentifier must not be empty");
        }

        long deletedOrders = orderRepository.deleteByUserIdentifier(userIdentifier);
        long deletedMessages = messageRepository.deleteByUserIdentifier(userIdentifier);

        return new ErasureResultDto(userIdentifier, deletedOrders, deletedMessages, "SUCCESS");
    }

    private AgentOrderDto mapOrderToDto(AgentOrder order) {
        return new AgentOrderDto(
                order.getId(),
                order.getUserIdentifier(),
                order.getServiceType(),
                order.getDetails(),
                order.getStatus(),
                order.getCreatedAt()
        );
    }

    private AgentMessageDto mapMessageToDto(AgentMessage message) {
        return new AgentMessageDto(
                message.getId(),
                message.getUserIdentifier(),
                message.getSenderName(),
                message.getMessageText(),
                message.getCreatedAt()
        );
    }
}
