package com.dmitryefremov.site.intake.service;

import com.dmitryefremov.site.intake.domain.ClientIntake;
import com.dmitryefremov.site.intake.dto.AgentMetadataDto;
import com.dmitryefremov.site.intake.dto.ClientIntakeRequestDto;
import com.dmitryefremov.site.intake.dto.ClientIntakeResponseDto;
import com.dmitryefremov.site.intake.repository.ClientIntakeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class ClientIntakeServiceTest {

    @Mock
    private ClientIntakeRepository clientIntakeRepository;

    private Clock fixedClock;
    private ClientIntakeService intakeService;

    private final Instant fixedInstant = Instant.parse("2026-10-15T12:00:00Z");

    @BeforeEach
    void setUp() {
        fixedClock = Clock.fixed(fixedInstant, ZoneOffset.UTC);
        intakeService = new ClientIntakeService(clientIntakeRepository, fixedClock);
    }

    @Test
    void testSubmitIntakeHumanClientSuccess() {
        ClientIntakeRequestDto request = new ClientIntakeRequestDto(
                "Alice Smith",
                "alice@example.com",
                "Acme Corp",
                "We need a turnkey AI automated pipeline for client intake with encrypted storage."
        );
        request.setClientType("HUMAN");
        request.setSource("WEB_PORTAL");

        ClientIntake savedEntity = new ClientIntake(
                "Alice Smith",
                "alice@example.com",
                "Acme Corp",
                "We need a turnkey AI automated pipeline for client intake with encrypted storage.",
                fixedClock
        );

        given(clientIntakeRepository.save(any(ClientIntake.class))).willReturn(savedEntity);

        ClientIntakeResponseDto response = intakeService.submitIntake(request);

        assertThat(response).isNotNull();
        assertThat(response.getClientType()).isEqualTo("HUMAN");
        assertThat(response.getClientName()).isEqualTo("Alice Smith");
        assertThat(response.getClientEmail()).isEqualTo("alice@example.com");
        assertThat(response.getCompanyName()).isEqualTo("Acme Corp");
        assertThat(response.getWishlistPayload()).isEqualTo("We need a turnkey AI automated pipeline for client intake with encrypted storage.");
        assertThat(response.getStatus()).isEqualTo("NEW");
        assertThat(response.getSource()).isEqualTo("WEB_PORTAL");
        assertThat(response.getCreatedAt()).isEqualTo(savedEntity.getCreatedAt());

        verify(clientIntakeRepository).save(any(ClientIntake.class));
    }

    @Test
    void testSubmitIntakeAgentClientWithMetadataSuccess() {
        ClientIntakeRequestDto request = new ClientIntakeRequestDto(
                "Agent System",
                "agent@example.com",
                "AI Partners",
                "Automated intake request with custom NDA payload."
        );
        request.setClientType("AGENT");
        request.setSource("A2A_AGENT");

        AgentMetadataDto agentMetadata = new AgentMetadataDto(
                "agent-turnkey-v1",
                "1.0.0",
                "https://agent.example.com/api/callback",
                List.of("AUDIT", "INTAKE_AUTO_PROCESS")
        );
        request.setAgentMetadata(agentMetadata);

        ClientIntake savedEntity = new ClientIntake(
                "Agent System",
                "agent@example.com",
                "AI Partners",
                "Automated intake request with custom NDA payload.",
                fixedClock
        );

        given(clientIntakeRepository.save(any(ClientIntake.class))).willReturn(savedEntity);

        ClientIntakeResponseDto response = intakeService.submitIntake(request);

        assertThat(response).isNotNull();
        assertThat(response.getClientType()).isEqualTo("AGENT");
        assertThat(response.getClientName()).isEqualTo("Agent System");
        assertThat(response.getClientEmail()).isEqualTo("agent@example.com");
        assertThat(response.getAgentMetadata()).isNotNull();
        assertThat(response.getAgentMetadata().getAgentId()).isEqualTo("agent-turnkey-v1");
        assertThat(response.getAgentMetadata().getCapabilities()).containsExactly("AUDIT", "INTAKE_AUTO_PROCESS");
        assertThat(response.getSource()).isEqualTo("A2A_AGENT");

        verify(clientIntakeRepository).save(any(ClientIntake.class));
    }
}
