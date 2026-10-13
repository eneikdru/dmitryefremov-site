package com.dmitryefremov.site.compliance;

import com.dmitryefremov.site.agent.domain.AgentMessage;
import com.dmitryefremov.site.agent.domain.AgentOrder;
import com.dmitryefremov.site.agent.repository.AgentMessageRepository;
import com.dmitryefremov.site.agent.repository.AgentOrderRepository;
import com.dmitryefremov.site.ai.domain.AiDirectMessage;
import com.dmitryefremov.site.ai.domain.AiServiceOrder;
import com.dmitryefremov.site.ai.repository.AiDirectMessageRepository;
import com.dmitryefremov.site.ai.repository.AiServiceOrderRepository;
import com.dmitryefremov.site.compliance.domain.DirectMessage;
import com.dmitryefremov.site.compliance.domain.ServiceOrder;
import com.dmitryefremov.site.compliance.dto.ErasureResponseDto;
import com.dmitryefremov.site.compliance.repository.DirectMessageRepository;
import com.dmitryefremov.site.compliance.repository.ServiceOrderRepository;
import com.dmitryefremov.site.compliance.service.UserDataComplianceService;
import com.dmitryefremov.site.intake.domain.ClientIntake;
import com.dmitryefremov.site.intake.repository.ClientIntakeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;

import java.time.OffsetDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class UserDataComplianceErasureVerificationTest {

    @Autowired
    private UserDataComplianceService complianceService;

    @Autowired
    private ServiceOrderRepository serviceOrderRepository;

    @Autowired
    private DirectMessageRepository directMessageRepository;

    @Autowired
    private AiServiceOrderRepository aiServiceOrderRepository;

    @Autowired
    private AiDirectMessageRepository aiDirectMessageRepository;

    @Autowired
    private AgentOrderRepository agentOrderRepository;

    @Autowired
    private AgentMessageRepository agentMessageRepository;

    @Autowired
    private ClientIntakeRepository clientIntakeRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private static final String TARGET_SUBJECT = "erasure-subject@example.com";
    private static final String CONTROL_SUBJECT = "control-subject@example.com";

    @BeforeEach
    void setUp() {
        serviceOrderRepository.deleteAll();
        directMessageRepository.deleteAll();
        aiServiceOrderRepository.deleteAll();
        aiDirectMessageRepository.deleteAll();
        agentOrderRepository.deleteAll();
        agentMessageRepository.deleteAll();
        clientIntakeRepository.deleteAll();
    }

    @Test
    @DisplayName("Erasure Protocol Verification: When an erasure operation is performed for a specific subject, SQL assertions prove zero residual identifying rows exist across all tables")
    void testErasureProtocolGuaranteesZeroResidualIdentifyingRows() {
        OffsetDateTime now = OffsetDateTime.now();

        // Populate Target Subject Data across all telemetry/compliance tables
        serviceOrderRepository.save(new ServiceOrder(TARGET_SUBJECT, "MVP", "Target order details", "PENDING", now));
        directMessageRepository.save(new DirectMessage(TARGET_SUBJECT, "Target direct message", now));
        aiServiceOrderRepository.save(new AiServiceOrder("Target Name", TARGET_SUBJECT, "Agent-1", "AUDIT", "Target AI details", now));
        aiDirectMessageRepository.save(new AiDirectMessage("Target Name", TARGET_SUBJECT, "Agent-1", "Target AI message content", now));
        agentOrderRepository.save(new AgentOrder("Target Client", TARGET_SUBJECT, "CONSULTING", "Target agent specs", "NEW", now));
        agentMessageRepository.save(new AgentMessage("Target Sender", TARGET_SUBJECT, "Target agent message", "SENT", now));
        clientIntakeRepository.save(new ClientIntake("Target Client", TARGET_SUBJECT, "Acme Corp", "Target intake wishlist"));

        // Populate Control Subject Data across all telemetry/compliance tables
        serviceOrderRepository.save(new ServiceOrder(CONTROL_SUBJECT, "MVP", "Control order details", "PENDING", now));
        directMessageRepository.save(new DirectMessage(CONTROL_SUBJECT, "Control direct message", now));
        aiServiceOrderRepository.save(new AiServiceOrder("Control Name", CONTROL_SUBJECT, "Agent-2", "AUDIT", "Control AI details", now));
        aiDirectMessageRepository.save(new AiDirectMessage("Control Name", CONTROL_SUBJECT, "Agent-2", "Control AI message content", now));
        agentOrderRepository.save(new AgentOrder("Control Client", CONTROL_SUBJECT, "CONSULTING", "Control agent specs", "NEW", now));
        agentMessageRepository.save(new AgentMessage("Control Sender", CONTROL_SUBJECT, "Control agent message", "SENT", now));
        clientIntakeRepository.save(new ClientIntake("Control Client", CONTROL_SUBJECT, "Beta Corp", "Control intake wishlist"));

        // Verify pre-erasure SQL counts for Target Subject
        assertThat(countRowsByColumn("service_orders", "user_id", TARGET_SUBJECT)).isEqualTo(1);
        assertThat(countRowsByColumn("direct_messages", "user_id", TARGET_SUBJECT)).isEqualTo(1);
        assertThat(countRowsByColumn("ai_service_orders", "user_email", TARGET_SUBJECT)).isEqualTo(1);
        assertThat(countRowsByColumn("ai_direct_messages", "sender_email", TARGET_SUBJECT)).isEqualTo(1);
        assertThat(countRowsByColumn("agent_orders", "client_email", TARGET_SUBJECT)).isEqualTo(1);
        assertThat(countRowsByColumn("agent_messages", "sender_email", TARGET_SUBJECT)).isEqualTo(1);
        assertThat(countRowsByColumn("client_intakes", "client_email", TARGET_SUBJECT)).isEqualTo(1);

        // Perform Erasure Protocol for Target Subject
        ErasureResponseDto response = complianceService.eraseUserData(TARGET_SUBJECT);

        assertThat(response.getUserId()).isEqualTo(TARGET_SUBJECT);
        assertThat(response.getOrdersDeleted()).isEqualTo(3);
        assertThat(response.getMessagesDeleted()).isEqualTo(3);

        // SQL Assertions: Prove EXACT ZERO residual identifying rows exist for Target Subject across all tables
        assertThat(countRowsByColumn("service_orders", "user_id", TARGET_SUBJECT)).isZero();
        assertThat(countRowsByColumn("direct_messages", "user_id", TARGET_SUBJECT)).isZero();
        assertThat(countRowsByColumn("ai_service_orders", "user_email", TARGET_SUBJECT)).isZero();
        assertThat(countRowsByColumn("ai_direct_messages", "sender_email", TARGET_SUBJECT)).isZero();
        assertThat(countRowsByColumn("agent_orders", "client_email", TARGET_SUBJECT)).isZero();
        assertThat(countRowsByColumn("agent_messages", "sender_email", TARGET_SUBJECT)).isZero();
        assertThat(countRowsByColumn("client_intakes", "client_email", TARGET_SUBJECT)).isZero();

        // SQL Assertions: Prove Control Subject data remains 100% intact and untouched
        assertThat(countRowsByColumn("service_orders", "user_id", CONTROL_SUBJECT)).isEqualTo(1);
        assertThat(countRowsByColumn("direct_messages", "user_id", CONTROL_SUBJECT)).isEqualTo(1);
        assertThat(countRowsByColumn("ai_service_orders", "user_email", CONTROL_SUBJECT)).isEqualTo(1);
        assertThat(countRowsByColumn("ai_direct_messages", "sender_email", CONTROL_SUBJECT)).isEqualTo(1);
        assertThat(countRowsByColumn("agent_orders", "client_email", CONTROL_SUBJECT)).isEqualTo(1);
        assertThat(countRowsByColumn("agent_messages", "sender_email", CONTROL_SUBJECT)).isEqualTo(1);
        assertThat(countRowsByColumn("client_intakes", "client_email", CONTROL_SUBJECT)).isEqualTo(1);
    }

    private int countRowsByColumn(String tableName, String columnName, String value) {
        String sql = String.format("SELECT COUNT(*) FROM %s WHERE %s = ?", tableName, columnName);
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, value);
        return count != null ? count : 0;
    }
}
