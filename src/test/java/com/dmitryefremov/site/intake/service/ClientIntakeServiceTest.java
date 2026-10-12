package com.dmitryefremov.site.intake.service;

import com.dmitryefremov.site.intake.domain.ClientIntake;
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
import java.time.ZoneId;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ClientIntakeServiceTest {

    @Mock
    private ClientIntakeRepository clientIntakeRepository;

    private Clock fixedClock;
    private ClientIntakeService clientIntakeService;

    @BeforeEach
    void setUp() {
        fixedClock = Clock.fixed(Instant.parse("2026-10-12T20:41:09Z"), ZoneId.of("UTC"));
        clientIntakeService = new ClientIntakeService(clientIntakeRepository, fixedClock);
    }

    @Test
    void createIntake_SavesEntityAndReturnsDtoWithInjectedClockTimestamp() {
        ClientIntakeRequestDto requestDto = new ClientIntakeRequestDto(
                "Alice Smith",
                "alice@example.com",
                "Acme Corp",
                "{\"idea\":\"Build AI Turnkey Platform\",\"wishlist\":[\"Secure intake\"]}"
        );

        when(clientIntakeRepository.save(any(ClientIntake.class))).thenAnswer(invocation -> {
            ClientIntake intake = invocation.getArgument(0);
            return intake;
        });

        ClientIntakeResponseDto response = clientIntakeService.createIntake(requestDto);

        assertThat(response.getClientName()).isEqualTo("Alice Smith");
        assertThat(response.getClientEmail()).isEqualTo("alice@example.com");
        assertThat(response.getCompanyName()).isEqualTo("Acme Corp");
        assertThat(response.getStatus()).isEqualTo("NEW");
        assertThat(response.getCreatedAt()).isNotNull();
        assertThat(response.getCreatedAt().toString()).contains("2026-10-12T20:41:09Z");

        verify(clientIntakeRepository).save(any(ClientIntake.class));
    }
}
