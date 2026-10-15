package com.dmitryefremov.site.intake.service;

import com.dmitryefremov.site.intake.domain.IntakeRequest;
import com.dmitryefremov.site.intake.dto.IntakeResponseDto;
import com.dmitryefremov.site.intake.dto.IntakeSubmissionDto;
import com.dmitryefremov.site.intake.event.IntakeRequestCreatedEvent;
import com.dmitryefremov.site.intake.repository.IntakeRequestRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import java.time.Clock;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class IntakeServiceTest {

    @Mock
    private IntakeRequestRepository intakeRequestRepository;

    @Mock
    private ApplicationEventPublisher eventPublisher;

    private Clock fixedClock;
    private IntakeService intakeService;

    @BeforeEach
    void setUp() {
        Instant fixedInstant = Instant.parse("2026-10-15T12:00:00Z");
        fixedClock = Clock.fixed(fixedInstant, ZoneOffset.UTC);

        intakeService = new IntakeService(intakeRequestRepository, eventPublisher, fixedClock);
    }

    @Test
    void testStandardIntakeSubmissionSucceedsWithCaptchaToken() {
        IntakeSubmissionDto dto = new IntakeSubmissionDto(
                "Jane Doe",
                "jane.doe@example.com",
                "Acme Corp",
                "{\"scope\": \"Security Audit\"}",
                false,
                "valid-captcha-token"
        );

        when(intakeRequestRepository.save(any(IntakeRequest.class))).thenAnswer(invocation -> {
            IntakeRequest req = invocation.getArgument(0);
            return req;
        });

        IntakeResponseDto response = intakeService.submitIntake(dto);

        assertThat(response).isNotNull();
        assertThat(response.getStatus()).isEqualTo("CONFIRMED");
        assertThat(response.getAcknowledgementMessage()).contains("Confidential audit intake request received successfully");
        assertThat(response.getAcknowledgementMessage()).contains("jane.doe@example.com");
        assertThat(response.getTimestamp()).isEqualTo(OffsetDateTime.now(fixedClock));

        ArgumentCaptor<IntakeRequest> captor = ArgumentCaptor.forClass(IntakeRequest.class);
        verify(intakeRequestRepository, times(1)).save(captor.capture());

        IntakeRequest saved = captor.getValue();
        assertThat(saved.getClientName()).isEqualTo("Jane Doe");
        assertThat(saved.getClientEmail()).isEqualTo("jane.doe@example.com");
        assertThat(saved.getCompanyName()).isEqualTo("Acme Corp");
        assertThat(saved.getAuditPayload()).isEqualTo("{\"scope\": \"Security Audit\"}");

        verify(eventPublisher, times(1)).publishEvent(any(IntakeRequestCreatedEvent.class));
    }

    @Test
    void testA2aIntakeSubmissionBypassesHumanCaptchaCheck() {
        IntakeSubmissionDto dto = new IntakeSubmissionDto(
                "Autonomous Agent 007",
                "agent.007@agentic.ai",
                "Agentic System",
                "{\"auditTarget\": \"A2A Gateway\"}",
                true,
                null // captcha token is null
        );

        when(intakeRequestRepository.save(any(IntakeRequest.class))).thenAnswer(invocation -> invocation.getArgument(0));

        IntakeResponseDto response = intakeService.submitIntake(dto);

        assertThat(response).isNotNull();
        assertThat(response.getStatus()).isEqualTo("CONFIRMED");
        assertThat(response.getAcknowledgementMessage()).contains("agent.007@agentic.ai");

        verify(intakeRequestRepository, times(1)).save(any(IntakeRequest.class));
        verify(eventPublisher, times(1)).publishEvent(any(IntakeRequestCreatedEvent.class));
    }

    @Test
    void testStandardIntakeWithoutCaptchaTokenFails() {
        IntakeSubmissionDto dto = new IntakeSubmissionDto(
                "Human Client",
                "human@example.com",
                "Human Corp",
                "{\"scope\": \"System Audit\"}",
                false,
                null
        );

        assertThatThrownBy(() -> intakeService.submitIntake(dto))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Human UI verification (captchaToken) is required");

        verifyNoInteractions(intakeRequestRepository);
    }

    @Test
    void testInvalidEmailFailsValidation() {
        IntakeSubmissionDto dto = new IntakeSubmissionDto(
                "Jane Doe",
                "invalid-email-address",
                "Acme Corp",
                "{\"scope\": \"Audit\"}",
                true,
                null
        );

        assertThatThrownBy(() -> intakeService.submitIntake(dto))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Invalid client email address");

        verifyNoInteractions(intakeRequestRepository);
    }
}
