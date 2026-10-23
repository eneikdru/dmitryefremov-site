package com.dmitryefremov.site.compliance;

import com.dmitryefremov.site.agent.repository.AgentMessageRepository;
import com.dmitryefremov.site.agent.repository.AgentOrderRepository;
import com.dmitryefremov.site.ai.repository.AiDirectMessageRepository;
import com.dmitryefremov.site.ai.repository.AiServiceOrderRepository;
import com.dmitryefremov.site.compliance.domain.DirectMessage;
import com.dmitryefremov.site.compliance.domain.ServiceOrder;
import com.dmitryefremov.site.compliance.dto.ErasureResponseDto;
import com.dmitryefremov.site.compliance.dto.UserDataExportDto;
import com.dmitryefremov.site.compliance.repository.DirectMessageRepository;
import com.dmitryefremov.site.compliance.repository.ServiceOrderRepository;
import com.dmitryefremov.site.compliance.service.UserDataComplianceService;
import com.dmitryefremov.site.intake.repository.ClientIntakeRepository;
import com.dmitryefremov.site.intake.repository.IntakeRequestRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserDataComplianceServiceTest {

    @Mock
    private ServiceOrderRepository serviceOrderRepository;

    @Mock
    private DirectMessageRepository directMessageRepository;

    @Mock
    private AiServiceOrderRepository aiServiceOrderRepository;

    @Mock
    private AiDirectMessageRepository aiDirectMessageRepository;

    @Mock
    private AgentOrderRepository agentOrderRepository;

    @Mock
    private AgentMessageRepository agentMessageRepository;

    @Mock
    private ClientIntakeRepository clientIntakeRepository;

    @Mock
    private IntakeRequestRepository intakeRequestRepository;

    private Clock fixedClock;
    private UserDataComplianceService complianceService;

    @BeforeEach
    void setUp() {
        Instant fixedInstant = Instant.parse("2026-10-12T12:00:00Z");
        fixedClock = Clock.fixed(fixedInstant, ZoneId.of("UTC"));
        complianceService = new UserDataComplianceService(
                serviceOrderRepository,
                directMessageRepository,
                aiServiceOrderRepository,
                aiDirectMessageRepository,
                agentOrderRepository,
                agentMessageRepository,
                clientIntakeRepository,
                intakeRequestRepository,
                fixedClock
        );
    }

    @Test
    @DisplayName("exportUserData should return user orders and direct messages with reproducible clock timestamp")
    void testExportUserData() {
        String userId = "agent-user-123";
        ServiceOrder order = new ServiceOrder(userId, "MVP", "MVP specification", "PENDING", fixedClock.instant().atOffset(ZoneOffset.UTC));
        DirectMessage message = new DirectMessage(userId, "Hello from ChatGPT agent", fixedClock.instant().atOffset(ZoneOffset.UTC));

        when(serviceOrderRepository.findByUserId(userId)).thenReturn(List.of(order));
        when(directMessageRepository.findByUserId(userId)).thenReturn(List.of(message));

        UserDataExportDto result = complianceService.exportUserData(userId);

        assertThat(result.getUserId()).isEqualTo(userId);
        assertThat(result.getOrders()).hasSize(1);
        assertThat(result.getOrders().get(0).getServiceType()).isEqualTo("MVP");
        assertThat(result.getMessages()).hasSize(1);
        assertThat(result.getMessages().get(0).getContent()).isEqualTo("Hello from ChatGPT agent");
        assertThat(result.getExportedAt()).isEqualTo(fixedClock.instant().atOffset(ZoneOffset.UTC));
    }

    @Test
    @DisplayName("eraseUserData should call deleteByUserId and related delete methods across all personal data repositories")
    void testEraseUserData() {
        String userId = "agent-user-456@example.com";
        when(serviceOrderRepository.deleteByUserId(userId)).thenReturn(2L);
        when(directMessageRepository.deleteByUserId(userId)).thenReturn(3L);
        when(aiServiceOrderRepository.deleteByUserEmail(userId)).thenReturn(1L);
        when(aiDirectMessageRepository.deleteBySenderEmail(userId)).thenReturn(2L);
        when(agentOrderRepository.deleteByClientEmail(userId)).thenReturn(1L);
        when(agentMessageRepository.deleteBySenderEmail(userId)).thenReturn(1L);
        when(clientIntakeRepository.deleteByClientEmail(userId)).thenReturn(1L);
        when(intakeRequestRepository.deleteByClientEmail(userId)).thenReturn(1L);

        ErasureResponseDto result = complianceService.eraseUserData(userId);

        assertThat(result.getUserId()).isEqualTo(userId);
        assertThat(result.getOrdersDeleted()).isEqualTo(4); // 2 + 1 + 1
        assertThat(result.getMessagesDeleted()).isEqualTo(6); // 3 + 2 + 1
        assertThat(result.getErasedAt()).isEqualTo(fixedClock.instant().atOffset(ZoneOffset.UTC));
        assertThat(result.getStatusMessage()).contains("permanently erased");

        verify(serviceOrderRepository, times(1)).deleteByUserId(userId);
        verify(directMessageRepository, times(1)).deleteByUserId(userId);
        verify(aiServiceOrderRepository, times(1)).deleteByUserEmail(userId);
        verify(aiDirectMessageRepository, times(1)).deleteBySenderEmail(userId);
        verify(agentOrderRepository, times(1)).deleteByClientEmail(userId);
        verify(agentMessageRepository, times(1)).deleteBySenderEmail(userId);
        verify(clientIntakeRepository, times(1)).deleteByClientEmail(userId);
        verify(intakeRequestRepository, times(1)).deleteByClientEmail(userId);
    }

    @Test
    @DisplayName("exportUserData should support querying by email address")
    void testExportUserDataByEmail() {
        String email = "user@example.com";
        ServiceOrder order = new ServiceOrder(email, "AUDIT", "Security audit request", "PENDING", fixedClock.instant().atOffset(ZoneOffset.UTC));
        DirectMessage message = new DirectMessage(email, "Compliance query", fixedClock.instant().atOffset(ZoneOffset.UTC));

        when(serviceOrderRepository.findByUserId(email)).thenReturn(List.of(order));
        when(directMessageRepository.findByUserId(email)).thenReturn(List.of(message));

        UserDataExportDto result = complianceService.exportUserData(email);

        assertThat(result.getUserId()).isEqualTo(email);
        assertThat(result.getOrders()).hasSize(1);
        assertThat(result.getOrders().get(0).getServiceType()).isEqualTo("AUDIT");
        assertThat(result.getMessages()).hasSize(1);
        assertThat(result.getMessages().get(0).getContent()).isEqualTo("Compliance query");
    }

    @Test
    @DisplayName("eraseUserData should support erasing by email address")
    void testEraseUserDataByEmail() {
        String email = "erase-me@example.com";
        when(serviceOrderRepository.deleteByUserId(email)).thenReturn(1L);
        when(directMessageRepository.deleteByUserId(email)).thenReturn(1L);

        ErasureResponseDto result = complianceService.eraseUserData(email);

        assertThat(result.getUserId()).isEqualTo(email);
        assertThat(result.getOrdersDeleted()).isEqualTo(1);
        assertThat(result.getMessagesDeleted()).isEqualTo(1);
    }

    @Test
    @DisplayName("exportUserData should throw IllegalArgumentException when userId is blank")
    void testExportUserDataValidation() {
        assertThatThrownBy(() -> complianceService.exportUserData("  "))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("User ID or email address cannot be null or empty");
    }
}
