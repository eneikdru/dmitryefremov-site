package com.dmitryefremov.site.ai.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AiAgentDocsController.class)
class AiAgentDocsControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void testGetLlmsTxtReturnsDocumentation() throws Exception {
        mockMvc.perform(get("/llms.txt"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_PLAIN))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("# Dmitry Efremov Personal Site & Service API for AI Agents")))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("POST /api/v1/ai/orders")))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("POST /api/v1/ai/messages")));
    }

    @Test
    void testGetRootReturnsDocumentation() throws Exception {
        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_PLAIN))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("# Dmitry Efremov Personal Site & Service API for AI Agents")));
    }

    @Test
    void testGetOpenApiSpecReturnsJsonSpec() throws Exception {
        mockMvc.perform(get("/api/v1/ai/docs"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.openapi").value("3.0.3"))
                .andExpect(jsonPath("$.info.title").value("AI Agent API Specification"))
                .andExpect(jsonPath("$.paths['/api/v1/ai/orders']").exists())
                .andExpect(jsonPath("$.paths['/api/v1/ai/messages']").exists());
    }
}
