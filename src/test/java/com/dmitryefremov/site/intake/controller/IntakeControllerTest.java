package com.dmitryefremov.site.intake.controller;

import com.dmitryefremov.site.intake.dto.IntakeResponseDto;
import com.dmitryefremov.site.intake.dto.IntakeSubmissionDto;
import com.dmitryefremov.site.intake.service.IntakeService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Clock;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(IntakeController.class)
class IntakeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private IntakeService intakeService;

    private Clock fixedClock;

    @BeforeEach
    void setUp() {
        fixedClock = Clock.fixed(Instant.parse("2026-10-15T12:00:00Z"), ZoneOffset.UTC);
    }

    @Test
    void testSubmitStandardIntakeReturnsCreated() throws Exception {
        IntakeSubmissionDto dto = new IntakeSubmissionDto(
                "Dmitry Client",
                "client@example.com",
                "Tech Solutions",
                "{\"audit\": \"Architecture Review\"}",
                false,
                "captcha-token-123"
        );

        IntakeResponseDto responseDto = new IntakeResponseDto(
                100L,
                "CONFIRMED",
                "Confidential audit intake request received successfully. Automatic acknowledgement dispatched to client@example.com",
                OffsetDateTime.now(fixedClock)
        );

        when(intakeService.submitIntake(any(IntakeSubmissionDto.class))).thenReturn(responseDto);

        mockMvc.perform(post("/api/v1/intake")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(100))
                .andExpect(jsonPath("$.status").value("CONFIRMED"))
                .andExpect(jsonPath("$.acknowledgementMessage").value(responseDto.getAcknowledgementMessage()))
                .andExpect(jsonPath("$.timestamp").isNotEmpty());
    }

    @Test
    void testSubmitA2aIntakeViaHeaderReturnsCreated() throws Exception {
        IntakeSubmissionDto dto = new IntakeSubmissionDto(
                "A2A System Agent",
                "a2a@partner.org",
                "Partner AI",
                "{\"auditTarget\": \"Agentic Pipeline\"}"
        );

        IntakeResponseDto responseDto = new IntakeResponseDto(
                101L,
                "CONFIRMED",
                "Confidential audit intake request received successfully. Automatic acknowledgement dispatched to a2a@partner.org",
                OffsetDateTime.now(fixedClock)
        );

        when(intakeService.submitIntake(any(IntakeSubmissionDto.class))).thenReturn(responseDto);

        mockMvc.perform(post("/api/v1/intake/audit")
                        .header("X-A2A-Gateway", "true")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(101))
                .andExpect(jsonPath("$.status").value("CONFIRMED"));
    }

    @Test
    void testJsonBodySpoofingIsA2aIsIgnoredByJackson() throws Exception {
        String jsonWithSpoofedA2a = """
                {
                  "clientName": "Spoofed Client",
                  "clientEmail": "spoof@example.com",
                  "companyName": "Spoof Inc",
                  "auditPayload": "Audit scope",
                  "isA2a": true,
                  "a2a": true
                }
                """;

        when(intakeService.submitIntake(any(IntakeSubmissionDto.class)))
                .thenThrow(new IllegalArgumentException("Human UI verification (captchaToken) is required for standard intake submissions"));

        mockMvc.perform(post("/api/v1/intake")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonWithSpoofedA2a))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Human UI verification (captchaToken) is required for standard intake submissions"));
    }

    @Test
    void testSubmitIntakeWithInvalidEmailReturnsBadRequest() throws Exception {
        IntakeSubmissionDto dto = new IntakeSubmissionDto(
                "Jane",
                "not-an-email",
                "Company",
                "Audit scope"
        );

        mockMvc.perform(post("/api/v1/intake")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Validation failed: clientEmail: Invalid email format"));
    }
}
