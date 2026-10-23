package com.dmitryefremov.site.intake.controller;

import com.dmitryefremov.site.intake.domain.ClientIntake;
import com.dmitryefremov.site.intake.repository.ClientIntakeRepository;
import com.dmitryefremov.site.intake.service.ClientIntakeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ClientIntakeController.class)
@Import({ClientIntakeService.class})
class ClientIntakeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ClientIntakeRepository clientIntakeRepository;

    @MockBean
    private Clock clock;

    private final Instant fixedInstant = Instant.parse("2026-10-15T12:00:00Z");

    @BeforeEach
    void setUp() {
        given(clock.instant()).willReturn(fixedInstant);
        given(clock.getZone()).willReturn(ZoneOffset.UTC);
        given(clock.millis()).willReturn(fixedInstant.toEpochMilli());
    }

    @Test
    void testSubmitIntakeSuccessReturnsCreatedAndIntakeResponse() throws Exception {
        ClientIntake savedIntake = new ClientIntake(
                "Alice Smith",
                "alice@example.com",
                "Acme Corp",
                "We need a turnkey AI automated pipeline for client intake with encrypted storage.",
                Clock.fixed(fixedInstant, ZoneOffset.UTC)
        );

        given(clientIntakeRepository.save(any(ClientIntake.class))).willReturn(savedIntake);

        String jsonPayload = """
                {
                  "clientType": "HUMAN",
                  "clientName": "Alice Smith",
                  "clientEmail": "alice@example.com",
                  "companyName": "Acme Corp",
                  "wishlistPayload": "We need a turnkey AI automated pipeline for client intake with encrypted storage.",
                  "source": "WEB_PORTAL"
                }
                """;

        mockMvc.perform(post("/api/intake")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonPayload))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.clientType").value("HUMAN"))
                .andExpect(jsonPath("$.clientName").value("Alice Smith"))
                .andExpect(jsonPath("$.clientEmail").value("alice@example.com"))
                .andExpect(jsonPath("$.companyName").value("Acme Corp"))
                .andExpect(jsonPath("$.wishlistPayload").value("We need a turnkey AI automated pipeline for client intake with encrypted storage."))
                .andExpect(jsonPath("$.status").value("NEW"))
                .andExpect(jsonPath("$.source").value("WEB_PORTAL"))
                .andExpect(jsonPath("$.createdAt").exists())
                .andExpect(jsonPath("$.updatedAt").exists());
    }

    @Test
    void testSubmitIntakeValidationFailureReturnsBadRequest() throws Exception {
        String invalidJsonPayload = """
                {
                  "clientType": "HUMAN",
                  "clientName": "",
                  "clientEmail": "invalid-email-format",
                  "wishlistPayload": "short"
                }
                """;

        mockMvc.perform(post("/api/intake")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidJsonPayload))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").value("Validation failed for intake request payload"))
                .andExpect(jsonPath("$.errors").isArray())
                .andExpect(jsonPath("$.timestamp").exists());
    }
}
