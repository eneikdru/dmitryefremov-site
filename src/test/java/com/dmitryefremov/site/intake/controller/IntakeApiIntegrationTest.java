package com.dmitryefremov.site.intake.controller;

import com.dmitryefremov.site.intake.domain.IntakeRequest;
import com.dmitryefremov.site.intake.dto.IntakeSubmissionDto;
import com.dmitryefremov.site.intake.repository.IntakeRequestRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class IntakeApiIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private IntakeRequestRepository intakeRequestRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    void testEndToEndIntakeSubmissionWithConfidentialEncryptionAtRest() throws Exception {
        String secretScope = "CONFIDENTIAL_AUDIT_SPECIFICATION_V2";
        IntakeSubmissionDto dto = new IntakeSubmissionDto(
                "Executive Client",
                "executive@client.org",
                "High Tech Corp",
                secretScope,
                false,
                "captcha-token-valid"
        );

        mockMvc.perform(post("/api/v1/intake")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.status").value("CONFIRMED"))
                .andExpect(jsonPath("$.acknowledgementMessage").value("Confidential audit intake request received successfully. Automatic acknowledgement dispatched to executive@client.org"));

        List<IntakeRequest> requests = intakeRequestRepository.findByClientEmail("executive@client.org");
        assertThat(requests).hasSize(1);
        IntakeRequest saved = requests.get(0);
        assertThat(saved.getAuditPayload()).isEqualTo(secretScope);

        // Verify raw database column is encrypted and does not contain plain secretScope text
        String rawDbPayload = jdbcTemplate.queryForObject(
                "SELECT audit_payload FROM intake_requests WHERE id = ?",
                String.class,
                saved.getId()
        );
        assertThat(rawDbPayload).isNotNull();
        assertThat(rawDbPayload).isNotEqualTo(secretScope);
        assertThat(rawDbPayload).doesNotContain("CONFIDENTIAL_AUDIT_SPECIFICATION_V2");
    }

    @Test
    void testA2aIntakeSubmissionEndToEndBypassesCaptcha() throws Exception {
        IntakeSubmissionDto dto = new IntakeSubmissionDto(
                "Agentic System Bot",
                "a2a-bot@network.ai",
                "Network AI",
                "{\"a2aAudit\": \"System Boundary Check\"}"
        );

        mockMvc.perform(post("/api/v1/intake/audit")
                        .header("X-A2A-Gateway", "1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("CONFIRMED"))
                .andExpect(jsonPath("$.acknowledgementMessage").value("Confidential audit intake request received successfully. Automatic acknowledgement dispatched to a2a-bot@network.ai"));

        List<IntakeRequest> requests = intakeRequestRepository.findByClientEmail("a2a-bot@network.ai");
        assertThat(requests).hasSize(1);
    }
}
