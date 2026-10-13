package com.dmitryefremov.site.telemetry.service;

import com.dmitryefremov.site.telemetry.domain.ScrollytellingProgression;
import com.dmitryefremov.site.telemetry.domain.TelemetryEvent;
import com.dmitryefremov.site.telemetry.dto.ScrollytellingIngestionRequest;
import com.dmitryefremov.site.telemetry.dto.ScrollytellingProgressionDto;
import com.dmitryefremov.site.telemetry.dto.TelemetryIngestionRequest;
import com.dmitryefremov.site.telemetry.dto.TelemetryIngestionResponse;
import com.dmitryefremov.site.telemetry.repository.ScrollytellingProgressionRepository;
import com.dmitryefremov.site.telemetry.repository.TelemetryEventRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.OffsetDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TelemetryIngestionServiceTest {

    @Mock
    private TelemetryEventRepository telemetryEventRepository;

    @Mock
    private ScrollytellingProgressionRepository scrollytellingProgressionRepository;

    private TelemetryIngestionService telemetryIngestionService;

    private final Instant fixedInstant = Instant.parse("2026-10-13T20:30:00Z");
    private final Clock fixedClock = Clock.fixed(fixedInstant, ZoneOffset.UTC);

    @BeforeEach
    void setUp() {
        telemetryIngestionService = new TelemetryIngestionService(telemetryEventRepository, scrollytellingProgressionRepository);
        telemetryIngestionService.setClock(fixedClock);
    }

    @Test
    @DisplayName("Ingest A2A consumption event and persist to timeseries datastore")
    void shouldIngestA2AConsumptionEventAndPersist() {
        TelemetryIngestionRequest request = new TelemetryIngestionRequest(
                "A2A_CONSUMPTION",
                "a2a-agent-client",
                "article-101",
                1.0,
                "{\"consumptionType\": \"full_read\"}"
        );

        TelemetryEvent mockSavedEvent = new TelemetryEvent(
                "A2A_CONSUMPTION",
                "a2a-agent-client",
                "article-101",
                1.0,
                "{\"consumptionType\": \"full_read\"}",
                OffsetDateTime.now(fixedClock)
        );
        mockSavedEvent.setId(42L);

        when(telemetryEventRepository.save(any(TelemetryEvent.class))).thenReturn(mockSavedEvent);

        TelemetryIngestionResponse response = telemetryIngestionService.ingestEvent(request);

        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(42L);
        assertThat(response.getEventType()).isEqualTo("A2A_CONSUMPTION");
        assertThat(response.getSource()).isEqualTo("a2a-agent-client");
        assertThat(response.getResourceId()).isEqualTo("article-101");
        assertThat(response.getMetricValue()).isCloseTo(1.0, within(0.0001));
        assertThat(response.getStatus()).isEqualTo("PROCESSED");

        ArgumentCaptor<TelemetryEvent> captor = ArgumentCaptor.forClass(TelemetryEvent.class);
        verify(telemetryEventRepository).save(captor.capture());
        TelemetryEvent captured = captor.getValue();
        assertThat(captured.getEventType()).isEqualTo("A2A_CONSUMPTION");
        assertThat(captured.getResourceId()).isEqualTo("article-101");
        assertThat(captured.getCreatedAt()).isEqualTo(OffsetDateTime.now(fixedClock));
    }

    @Test
    @DisplayName("Process scrollytelling depth and increment progression metric")
    void shouldProcessScrollytellingDepthAndIncrementProgression() {
        ScrollytellingIngestionRequest request = new ScrollytellingIngestionRequest(
                "essay-scrollytelling-2",
                75.5,
                "web-scrolly-view"
        );

        when(scrollytellingProgressionRepository.incrementProgressionAtomic(eq("essay-scrollytelling-2"), eq(75.5), any(OffsetDateTime.class)))
                .thenReturn(1);

        ScrollytellingProgression progression = new ScrollytellingProgression(
                "essay-scrollytelling-2",
                75.5,
                150.0,
                2,
                OffsetDateTime.now(fixedClock)
        );
        progression.setId(10L);

        when(scrollytellingProgressionRepository.findByResourceId("essay-scrollytelling-2"))
                .thenReturn(Optional.of(progression));

        ScrollytellingProgressionDto result = telemetryIngestionService.processScrollytellingDepth(request);

        assertThat(result.getResourceId()).isEqualTo("essay-scrollytelling-2");
        assertThat(result.getMaxDepth()).isCloseTo(75.5, within(0.0001));
        assertThat(result.getTotalDepth()).isCloseTo(150.0, within(0.0001));
        assertThat(result.getEventCount()).isEqualTo(2L);

        verify(telemetryEventRepository).save(any(TelemetryEvent.class));
        verify(scrollytellingProgressionRepository).incrementProgressionAtomic(eq("essay-scrollytelling-2"), eq(75.5), any(OffsetDateTime.class));
    }
}
