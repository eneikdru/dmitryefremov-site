package com.dmitryefremov.site.intake.controller;

import com.dmitryefremov.site.intake.dto.ClientIntakeResponseDto;
import com.dmitryefremov.site.intake.repository.ClientIntakeRepository;
import com.dmitryefremov.site.intake.service.ClientIntakeService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.web.servlet.MockMvc;

import java.time.OffsetDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class ClientIntakeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ClientIntakeRepository clientIntakeRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    void submitWishlist_ValidPayload_Returns201AndEncryptsAtRest() throws Exception {
        String jsonRequest = """
                {
                  "clientName": "Bob Vance",
                  "clientEmail": "bob@vancerefrigeration.com",
                  "companyName": "Vance Refrigeration",
                  "wishlistPayload": "{\\"idea\\":\\"Refrigeration Automation\\",\\"wishlist\\":[\\"Confidential NDA Work\\"]}"
                }
                """;

        mockMvc.perform(post("/api/v1/intake/wishlists")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.clientName").value("Bob Vance"))
                .andExpect(jsonPath("$.clientEmail").value("bob@vancerefrigeration.com"))
                .andExpect(jsonPath("$.companyName").value("Vance Refrigeration"))
                .andExpect(jsonPath("$.status").value("NEW"))
                .andExpect(jsonPath("$.createdAt").exists());

        var list = clientIntakeRepository.findByClientEmail("bob@vancerefrigeration.com");
        assertThat(list).hasSize(1);
        assertThat(list.get(0).getWishlistPayload()).contains("Confidential NDA Work");

        String rawDbPayload = jdbcTemplate.queryForObject(
                "SELECT wishlist_payload FROM client_intakes WHERE client_email = ?",
                String.class,
                "bob@vancerefrigeration.com"
        );

        assertThat(rawDbPayload).isNotNull();
        assertThat(rawDbPayload).doesNotContain("Confidential NDA Work");
        assertThat(rawDbPayload).doesNotContain("Refrigeration Automation");
    }

    @Test
    void submitWishlist_InvalidEmail_Returns400BadRequest() throws Exception {
        String jsonRequest = """
                {
                  "clientName": "Bob Vance",
                  "clientEmail": "not-an-email",
                  "wishlistPayload": "Some content"
                }
                """;

        mockMvc.perform(post("/api/v1/intake/wishlists")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").value(org.hamcrest.Matchers.containsString("clientEmail")));
    }

    @Test
    void submitWishlist_MissingRequiredFields_Returns400BadRequest() throws Exception {
        String jsonRequest = """
                {
                  "companyName": "Acme Corp"
                }
                """;

        mockMvc.perform(post("/api/v1/intake/wishlists")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));
    }
}
