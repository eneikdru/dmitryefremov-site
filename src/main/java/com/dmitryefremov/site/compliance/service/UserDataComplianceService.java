package com.dmitryefremov.site.compliance.service;

import com.dmitryefremov.site.compliance.domain.DirectMessage;
import com.dmitryefremov.site.compliance.domain.ServiceOrder;
import com.dmitryefremov.site.compliance.dto.*;
import com.dmitryefremov.site.compliance.repository.DirectMessageRepository;
import com.dmitryefremov.site.compliance.repository.ServiceOrderRepository;
import com.dmitryefremov.site.intake.repository.ClientIntakeRepository;
import com.dmitryefremov.site.ai.repository.AiServiceOrderRepository;
import com.dmitryefremov.site.ai.repository.AiDirectMessageRepository;
import com.dmitryefremov.site.agent.repository.AgentOrderRepository;
import com.dmitryefremov.site.agent.repository.AgentMessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service("siteUserDataComplianceService")
public class UserDataComplianceService {

    private final ServiceOrderRepository serviceOrderRepository;
    private final DirectMessageRepository directMessageRepository;
    private final ClientIntakeRepository clientIntakeRepository;
    private final AiServiceOrderRepository aiServiceOrderRepository;
    private final AiDirectMessageRepository aiDirectMessageRepository;
    private final AgentOrderRepository agentOrderRepository;
    private final AgentMessageRepository agentMessageRepository;
    private final Clock clock;

    @Autowired
    public UserDataComplianceService(ServiceOrderRepository serviceOrderRepository,
                                     DirectMessageRepository directMessageRepository,
                                     ClientIntakeRepository clientIntakeRepository,
                                     AiServiceOrderRepository aiServiceOrderRepository,
                                     AiDirectMessageRepository aiDirectMessageRepository,
                                     AgentOrderRepository agentOrderRepository,
                                     AgentMessageRepository agentMessageRepository) {
        this(serviceOrderRepository, directMessageRepository, clientIntakeRepository,
             aiServiceOrderRepository, aiDirectMessageRepository, agentOrderRepository, agentMessageRepository, Clock.systemUTC());
    }

    public UserDataComplianceService(ServiceOrderRepository serviceOrderRepository,
                                     DirectMessageRepository directMessageRepository,
                                     ClientIntakeRepository clientIntakeRepository,
                                     AiServiceOrderRepository aiServiceOrderRepository,
                                     AiDirectMessageRepository aiDirectMessageRepository,
                                     AgentOrderRepository agentOrderRepository,
                                     AgentMessageRepository agentMessageRepository,
                                     Clock clock) {
        this.serviceOrderRepository = serviceOrderRepository;
        this.directMessageRepository = directMessageRepository;
        this.clientIntakeRepository = clientIntakeRepository;
        this.aiServiceOrderRepository = aiServiceOrderRepository;
        this.aiDirectMessageRepository = aiDirectMessageRepository;
        this.agentOrderRepository = agentOrderRepository;
        this.agentMessageRepository = agentMessageRepository;
        this.clock = clock != null ? clock : Clock.systemUTC();
    }

    public Clock getClock() {
        return clock;
    }

    @Transactional(readOnly = true)
    public UserDataExportDto exportUserData(String userId) {
        validateUserId(userId);

        List<ServiceOrderDto> orders = serviceOrderRepository.findByUserId(userId)
                .stream()
                .map(this::toOrderDto)
                .collect(Collectors.toList());

        List<DirectMessageDto> messages = directMessageRepository.findByUserId(userId)
                .stream()
                .map(this::toMessageDto)
                .collect(Collectors.toList());

        OffsetDateTime now = OffsetDateTime.now(clock);
        return new UserDataExportDto(userId, orders, messages, now);
    }

    @Transactional
    public ErasureResponseDto eraseUserData(String userId) {
        validateUserId(userId);

        long ordersDeleted = serviceOrderRepository.deleteByUserId(userId);
        long messagesDeleted = directMessageRepository.deleteByUserId(userId);

        // Delete from all other tables containing personal data
        clientIntakeRepository.deleteByClientEmail(userId);
        aiServiceOrderRepository.deleteByUserEmail(userId);
        aiDirectMessageRepository.deleteBySenderEmail(userId);
        agentOrderRepository.deleteByClientEmail(userId);
        agentMessageRepository.deleteBySenderEmail(userId);

        OffsetDateTime now = OffsetDateTime.now(clock);
        String msg = "All personal data for user '" + userId + "' has been permanently erased.";
        return new ErasureResponseDto(userId, ordersDeleted, messagesDeleted, now, msg);
    }

    @Transactional
    public ServiceOrderDto createServiceOrder(String userId, String serviceType, String details) {
        validateUserId(userId);
        if (serviceType == null || serviceType.isBlank()) {
            throw new IllegalArgumentException("Service type cannot be null or empty");
        }

        ServiceOrder order = new ServiceOrder();
        order.setUserId(userId);
        order.setServiceType(serviceType);
        order.setDetails(details);
        order.setStatus("PENDING");
        order.setCreatedAt(OffsetDateTime.now(clock));

        ServiceOrder saved = serviceOrderRepository.save(order);
        return toOrderDto(saved);
    }

    @Transactional
    public DirectMessageDto createDirectMessage(String userId, String content) {
        validateUserId(userId);
        if (content == null || content.isBlank()) {
            throw new IllegalArgumentException("Message content cannot be null or empty");
        }

        DirectMessage message = new DirectMessage();
        message.setUserId(userId);
        message.setContent(content);
        message.setCreatedAt(OffsetDateTime.now(clock));

        DirectMessage saved = directMessageRepository.save(message);
        return toMessageDto(saved);
    }

    private void validateUserId(String userId) {
        if (userId == null || userId.trim().isEmpty()) {
            throw new IllegalArgumentException("User ID or email address cannot be null or empty");
        }
    }

    private ServiceOrderDto toOrderDto(ServiceOrder order) {
        return new ServiceOrderDto(
                order.getId(),
                order.getUserId(),
                order.getServiceType(),
                order.getDetails(),
                order.getStatus(),
                order.getCreatedAt()
        );
    }

    private DirectMessageDto toMessageDto(DirectMessage message) {
        return new DirectMessageDto(
                message.getId(),
                message.getUserId(),
                message.getContent(),
                message.getCreatedAt()
        );
    }
}
