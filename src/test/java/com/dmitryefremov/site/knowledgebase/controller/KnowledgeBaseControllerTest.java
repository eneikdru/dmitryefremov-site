package com.dmitryefremov.site.knowledgebase.controller;

import com.dmitryefremov.site.knowledgebase.dto.AppliedModelConfigurationDto;
import com.dmitryefremov.site.knowledgebase.dto.ConceptCreateRequestDto;
import com.dmitryefremov.site.knowledgebase.dto.ConceptDetailDto;
import com.dmitryefremov.site.knowledgebase.dto.ConceptItemDto;
import com.dmitryefremov.site.knowledgebase.dto.ConceptPageDto;
import com.dmitryefremov.site.knowledgebase.exception.ResourceNotFoundException;
import com.dmitryefremov.site.knowledgebase.service.KnowledgeBaseService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = KnowledgeBaseController.class)
public class KnowledgeBaseControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private KnowledgeBaseService knowledgeBaseService;

    // Required for security config/interceptors in context
    @MockBean
    private PasswordEncoder passwordEncoder;

    @Test
    void getConcepts_shouldReturnPage() throws Exception {
        ConceptPageDto mockPage = new ConceptPageDto();
        ConceptItemDto item = new ConceptItemDto();
        item.setSlug("test-slug");
        item.setTitle("Test Concept");
        mockPage.setContent(List.of(item));
        mockPage.setPage(0);
        mockPage.setSize(20);
        mockPage.setTotalElements(1L);
        mockPage.setTotalPages(1);

        when(knowledgeBaseService.getConcepts(any(), any(), any(), anyInt(), anyInt())).thenReturn(mockPage);

        mockMvc.perform(get("/api/v1/knowledge-base/concepts"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].slug").value("test-slug"))
                .andExpect(jsonPath("$.totalElements").value(1));
    }

    @Test
    void createConcept_shouldReturnCreated() throws Exception {
        ConceptCreateRequestDto request = new ConceptCreateRequestDto();
        request.setSlug("new-slug");
        request.setTitle("New Title");
        request.setCategory("TEST");

        ConceptItemDto response = new ConceptItemDto();
        response.setSlug("new-slug");

        when(knowledgeBaseService.createConcept(any())).thenReturn(response);

        mockMvc.perform(post("/api/v1/knowledge-base/concepts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.slug").value("new-slug"));
    }

    @Test
    void getConceptBySlug_shouldReturnDetail() throws Exception {
        ConceptDetailDto detail = new ConceptDetailDto();
        detail.setSlug("some-slug");
        when(knowledgeBaseService.getConceptBySlug(eq("some-slug"))).thenReturn(detail);

        mockMvc.perform(get("/api/v1/knowledge-base/concepts/some-slug"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.slug").value("some-slug"));
    }

    @Test
    void getConceptBySlug_notFound_shouldReturn404() throws Exception {
        when(knowledgeBaseService.getConceptBySlug(eq("missing"))).thenThrow(new ResourceNotFoundException("Not found"));

        mockMvc.perform(get("/api/v1/knowledge-base/concepts/missing"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404));
    }
}
