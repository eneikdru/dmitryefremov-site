package com.dmitryefremov.site.telemetry.service;

import com.dmitryefremov.site.admin.domain.TelemetryLog;
import com.dmitryefremov.site.admin.repository.TelemetryLogRepository;
import com.dmitryefremov.site.telemetry.dto.TelemetryIngestRequestDto;
import com.dmitryefremov.site.telemetry.dto.TelemetryLogDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZoneOffset;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AgentTelemetryServiceTest {

    @Mock
    private TelemetryLogRepository telemetryLogRepository;

    private Clock fixedClock;
    private AgentTelemetryService telemetryService;

    @BeforeEach
    void setUp() {
        fixedClock = Clock.fixed(Instant.parse("2026-10-15T14:00:00Z"), ZoneId.of("UTC"));
        telemetryService = new AgentTelemetryService(telemetryLogRepository, fixedClock);
    }

    @Test
    void ingestTelemetry_shouldPersistLogWithSeededClockTimestamp() {
        TelemetryIngestRequestDto request = new TelemetryIngestRequestDto("A2A_CONSUMPTION", "{\"detail\":\"test\"}", "a2a-service");

        when(telemetryLogRepository.save(any(TelemetryLog.class))).thenAnswer(invocation -> {
            TelemetryLog log = invocation.getArgument(0);
            log.setId(42L);
            return log;
        });

        TelemetryLogDto result = telemetryService.ingestTelemetry(request);

        assertThat(result.getId()).isEqualTo(42L);
        assertThat(result.getEventType()).isEqualTo("A2A_CONSUMPTION");
        assertThat(result.getPayload()).isEqualTo("{\"detail\":\"test\"}");
        assertThat(result.getSource()).isEqualTo("a2a-service");
        assertThat(result.getCreatedAt()).isEqualTo(Instant.parse("2026-10-15T14:00:00Z").atOffset(ZoneOffset.UTC));

        ArgumentCaptor<TelemetryLog> captor = ArgumentCaptor.forClass(TelemetryLog.class);
        verify(telemetryLogRepository).save(captor.capture());
        TelemetryLog saved = captor.getValue();
        assertThat(saved.getCreatedAt()).isEqualTo(Instant.parse("2026-10-15T14:00:00Z").atOffset(ZoneOffset.UTC));
    }

    @Test
    void ingestTelemetry_shouldThrowWhenEventTypeIsNull() {
        TelemetryIngestRequestDto request = new TelemetryIngestRequestDto(null, "payload", "source");

        assertThatThrownBy(() -> telemetryService.ingestTelemetry(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Event type cannot be empty");
    }
}
