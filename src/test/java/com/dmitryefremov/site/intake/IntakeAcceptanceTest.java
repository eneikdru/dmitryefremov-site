package com.dmitryefremov.site.intake;

import com.dmitryefremov.site.intake.repository.ClientIntakeRepository;
import com.dmitryefremov.site.intake.repository.IntakeRequestRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class IntakeAcceptanceTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ClientIntakeRepository clientIntakeRepository;

    @Autowired
    private IntakeRequestRepository intakeRequestRepository;

    @BeforeEach
    void setUp() {
        clientIntakeRepository.deleteAll();
        intakeRequestRepository.deleteAll();
    }

    @Test
    void givenHumanIntakeRequest_whenSimulated_thenDatabaseRecordsItAndMockInterceptsDispatch() throws Exception {
        String payload = """
                {
                    "classification": "confidential",
                    "clientEmail": "human@example.com",
                    "title": "Human Intake Title",
                    "narrative": "Human intake narrative here.",
                    "discardMetadata": true,
                    "requireRecoveryPhrase": true
                }
                """;

        mockMvc.perform(post("/api/intake")
                .contentType(MediaType.APPLICATION_JSON)
                .content(payload))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("ACKNOWLEDGED"))
                .andExpect(jsonPath("$.receiptId").exists());

        assertThat(clientIntakeRepository.findByClientEmail("human@example.com")).hasSize(1);
        assertThat(intakeRequestRepository.findByClientEmail("human@example.com")).isEmpty();
    }

    @Test
    void givenA2ASimulatedPayload_whenSentDirectlyToApi_thenSuccessfullyProcessedAndAcknowledgedIdentically() throws Exception {
        String payload = """
                {
                    "clientEmail": "a2a@example.com",
                    "clientName": "A2A Agent",
                    "companyName": "AI Corp",
                    "auditPayload": "{\\"action\\": \\"scan\\"}"
                }
                """;

        mockMvc.perform(post("/api/intake")
                .contentType(MediaType.APPLICATION_JSON)
                .content(payload))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("ACKNOWLEDGED"))
                .andExpect(jsonPath("$.receiptId").exists());

        assertThat(intakeRequestRepository.findByClientEmail("a2a@example.com")).hasSize(1);
        assertThat(clientIntakeRepository.findByClientEmail("a2a@example.com")).isEmpty();
    }
}
