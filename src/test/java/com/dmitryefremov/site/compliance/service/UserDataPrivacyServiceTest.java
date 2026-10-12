package com.dmitryefremov.site.compliance.service;

import com.dmitryefremov.site.compliance.domain.AgentMessage;
import com.dmitryefremov.site.compliance.domain.AgentOrder;
import com.dmitryefremov.site.compliance.dto.ErasureResultDto;
import com.dmitryefremov.site.compliance.dto.UserDataExportDto;
import com.dmitryefremov.site.compliance.repository.AgentMessageRepository;
import com.dmitryefremov.site.compliance.repository.AgentOrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Clock;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserDataPrivacyServiceTest {

    @Mock
    private AgentOrderRepository orderRepository;

    @Mock
    private AgentMessageRepository messageRepository;

    private Clock clock;
    private UserDataPrivacyService privacyService;

    @BeforeEach
    void setUp() {
        clock = Clock.fixed(Instant.parse("2026-10-12T12:00:00Z"), ZoneId.of("UTC"));
        privacyService = new UserDataPrivacyService(orderRepository, messageRepository, clock);
    }

    @Test
    void exportUserData_returnsAllOrdersAndMessagesForUser() {
        String user = "user-123";
        OffsetDateTime now = OffsetDateTime.now(clock);

        AgentOrder order = new AgentOrder(user, "AUDIT", "Audit request", "CREATED", now);
        order.setId(10L);

        AgentMessage message = new AgentMessage(user, "Agent Alice", "Hello author", now);
        message.setId(20L);

        when(orderRepository.findByUserIdentifier(user)).thenReturn(List.of(order));
        when(messageRepository.findByUserIdentifier(user)).thenReturn(List.of(message));

        UserDataExportDto exportDto = privacyService.exportUserData(user);

        assertThat(exportDto.getUserIdentifier()).isEqualTo(user);
        assertThat(exportDto.getOrders()).hasSize(1);
        assertThat(exportDto.getOrders().get(0).getServiceType()).isEqualTo("AUDIT");
        assertThat(exportDto.getMessages()).hasSize(1);
        assertThat(exportDto.getMessages().get(0).getMessageText()).isEqualTo("Hello author");
    }

    @Test
    void exportUserData_throwsException_whenUserIdentifierIsEmpty() {
        assertThatThrownBy(() -> privacyService.exportUserData("   "))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("userIdentifier must not be empty");
    }

    @Test
    void eraseUserData_deletesOrdersAndMessagesAndReturnsCounts() {
        String user = "user-123";

        when(orderRepository.deleteByUserIdentifier(user)).thenReturn(2L);
        when(messageRepository.deleteByUserIdentifier(user)).thenReturn(3L);

        ErasureResultDto result = privacyService.eraseUserData(user);

        assertThat(result.getUserIdentifier()).isEqualTo(user);
        assertThat(result.getDeletedOrdersCount()).isEqualTo(2L);
        assertThat(result.getDeletedMessagesCount()).isEqualTo(3L);
        assertThat(result.getStatus()).isEqualTo("SUCCESS");

        verify(orderRepository).deleteByUserIdentifier(user);
        verify(messageRepository).deleteByUserIdentifier(user);
    }

    @Test
    void eraseUserData_throwsException_whenUserIdentifierIsEmpty() {
        assertThatThrownBy(() -> privacyService.eraseUserData(""))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("userIdentifier must not be empty");
    }
}
