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
import com.dmitryefremov.site.intake.domain.IntakeRequest;
import com.dmitryefremov.site.intake.repository.ClientIntakeRepository;
import com.dmitryefremov.site.intake.repository.IntakeRequestRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;

import java.time.Clock;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class UserDataErasureVerificationTest {

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
    private IntakeRequestRepository intakeRequestRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private static final String TARGET_SUBJECT = "erasure-target@example.com";
    private static final String CONTROL_SUBJECT = "erasure-control@example.com";

    @BeforeEach
    void setUp() {
        serviceOrderRepository.deleteAll();
        directMessageRepository.deleteAll();
        aiServiceOrderRepository.deleteAll();
        aiDirectMessageRepository.deleteAll();
        agentOrderRepository.deleteAll();
        agentMessageRepository.deleteAll();
        clientIntakeRepository.deleteAll();
        intakeRequestRepository.deleteAll();
    }

    @Test
    @DisplayName("When erasure operation is performed for target subject, SQL assertions prove zero residual identifying rows exist across telemetry & compliance tables")
    void testTotalDataErasureForTargetSubjectWithSqlAssertions() {
        Clock fixedClock = Clock.fixed(Instant.parse("2026-10-20T10:00:00Z"), ZoneOffset.UTC);
        OffsetDateTime fixedTime = OffsetDateTime.now(fixedClock);

        // 1. Populate tables via saveAndFlush so JDBC assertions see flushed rows
        serviceOrderRepository.saveAndFlush(new ServiceOrder(TARGET_SUBJECT, "MVP", "Target order 1", "PENDING", fixedTime));
        serviceOrderRepository.saveAndFlush(new ServiceOrder(TARGET_SUBJECT, "AUDIT", "Target order 2", "PENDING", fixedTime));
        directMessageRepository.saveAndFlush(new DirectMessage(TARGET_SUBJECT, "Target direct message 1", fixedTime));

        aiServiceOrderRepository.saveAndFlush(new AiServiceOrder("Target User", TARGET_SUBJECT, "Agent-01", "MVP", "AI Specs", fixedTime));
        aiDirectMessageRepository.saveAndFlush(new AiDirectMessage("Target User", TARGET_SUBJECT, "Agent-01", "AI Message", fixedTime));

        agentOrderRepository.saveAndFlush(new AgentOrder("Target User", TARGET_SUBJECT, "AUTOMATION", "Agent Specs", "NEW", fixedTime));
        agentMessageRepository.saveAndFlush(new AgentMessage("Target User", TARGET_SUBJECT, "Agent Msg", "NEW", fixedTime));

        clientIntakeRepository.saveAndFlush(new ClientIntake("Target User", TARGET_SUBJECT, "Acme Corp", "Intake details", fixedClock));
        intakeRequestRepository.saveAndFlush(new IntakeRequest("Target Client", TARGET_SUBJECT, "Consulting", "Request details", fixedClock));

        // 2. Populate tables for CONTROL_SUBJECT
        serviceOrderRepository.saveAndFlush(new ServiceOrder(CONTROL_SUBJECT, "MVP", "Control order 1", "PENDING", fixedTime));
        directMessageRepository.saveAndFlush(new DirectMessage(CONTROL_SUBJECT, "Control message 1", fixedTime));

        aiServiceOrderRepository.saveAndFlush(new AiServiceOrder("Control User", CONTROL_SUBJECT, "Agent-01", "MVP", "AI Control Specs", fixedTime));
        aiDirectMessageRepository.saveAndFlush(new AiDirectMessage("Control User", CONTROL_SUBJECT, "Agent-01", "AI Control Message", fixedTime));

        agentOrderRepository.saveAndFlush(new AgentOrder("Control User", CONTROL_SUBJECT, "AUTOMATION", "Agent Control Specs", "NEW", fixedTime));
        agentMessageRepository.saveAndFlush(new AgentMessage("Control User", CONTROL_SUBJECT, "Agent Control Msg", "NEW", fixedTime));

        clientIntakeRepository.saveAndFlush(new ClientIntake("Control User", CONTROL_SUBJECT, "Beta Corp", "Control Intake details", fixedClock));
        intakeRequestRepository.saveAndFlush(new IntakeRequest("Control Client", CONTROL_SUBJECT, "Audit", "Control Request details", fixedClock));

        // Pre-erasure verification using raw SQL assertions
        assertThat(countRowsBySql("service_orders", "user_id", TARGET_SUBJECT)).isEqualTo(2);
        assertThat(countRowsBySql("direct_messages", "user_id", TARGET_SUBJECT)).isEqualTo(1);
        assertThat(countRowsBySql("ai_service_orders", "user_email", TARGET_SUBJECT)).isEqualTo(1);
        assertThat(countRowsBySql("ai_direct_messages", "sender_email", TARGET_SUBJECT)).isEqualTo(1);
        assertThat(countRowsBySql("agent_orders", "client_email", TARGET_SUBJECT)).isEqualTo(1);
        assertThat(countRowsBySql("agent_messages", "sender_email", TARGET_SUBJECT)).isEqualTo(1);
        assertThat(countRowsBySql("client_intakes", "client_email", TARGET_SUBJECT)).isEqualTo(1);
        assertThat(countRowsBySql("intake_requests", "client_email", TARGET_SUBJECT)).isEqualTo(1);

        // Act: Execute erasure operation
        ErasureResponseDto response = complianceService.eraseUserData(TARGET_SUBJECT);

        assertThat(response.getUserId()).isEqualTo(TARGET_SUBJECT);
        assertThat(response.getOrdersDeleted()).isEqualTo(4); // 2 service_orders + 1 ai_service_orders + 1 agent_orders
        assertThat(response.getMessagesDeleted()).isEqualTo(3); // 1 direct_messages + 1 ai_direct_messages + 1 agent_messages

        // Post-erasure SQL Assertions: Prove ZERO residual identifying rows exist for TARGET_SUBJECT
        assertThat(countRowsBySql("service_orders", "user_id", TARGET_SUBJECT)).isZero();
        assertThat(countRowsBySql("direct_messages", "user_id", TARGET_SUBJECT)).isZero();
        assertThat(countRowsBySql("ai_service_orders", "user_email", TARGET_SUBJECT)).isZero();
        assertThat(countRowsBySql("ai_direct_messages", "sender_email", TARGET_SUBJECT)).isZero();
        assertThat(countRowsBySql("agent_orders", "client_email", TARGET_SUBJECT)).isZero();
        assertThat(countRowsBySql("agent_messages", "sender_email", TARGET_SUBJECT)).isZero();
        assertThat(countRowsBySql("client_intakes", "client_email", TARGET_SUBJECT)).isZero();
        assertThat(countRowsBySql("intake_requests", "client_email", TARGET_SUBJECT)).isZero();

        // Control Subject Verification: Prove CONTROL_SUBJECT data remains completely intact
        assertThat(countRowsBySql("service_orders", "user_id", CONTROL_SUBJECT)).isEqualTo(1);
        assertThat(countRowsBySql("direct_messages", "user_id", CONTROL_SUBJECT)).isEqualTo(1);
        assertThat(countRowsBySql("ai_service_orders", "user_email", CONTROL_SUBJECT)).isEqualTo(1);
        assertThat(countRowsBySql("ai_direct_messages", "sender_email", CONTROL_SUBJECT)).isEqualTo(1);
        assertThat(countRowsBySql("agent_orders", "client_email", CONTROL_SUBJECT)).isEqualTo(1);
        assertThat(countRowsBySql("agent_messages", "sender_email", CONTROL_SUBJECT)).isEqualTo(1);
        assertThat(countRowsBySql("client_intakes", "client_email", CONTROL_SUBJECT)).isEqualTo(1);
        assertThat(countRowsBySql("intake_requests", "client_email", CONTROL_SUBJECT)).isEqualTo(1);
    }

    private Integer countRowsBySql(String tableName, String columnName, String identifier) {
        String sql = "SELECT COUNT(*) FROM " + tableName + " WHERE " + columnName + " = ?";
        return jdbcTemplate.queryForObject(sql, Integer.class, identifier);
    }
}
