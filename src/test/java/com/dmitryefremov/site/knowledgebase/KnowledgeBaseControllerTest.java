package com.dmitryefremov.site.knowledgebase;

import com.dmitryefremov.site.knowledgebase.controller.KnowledgeBaseController;
import com.dmitryefremov.site.knowledgebase.dto.*;
import com.dmitryefremov.site.knowledgebase.service.KnowledgeBaseService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(KnowledgeBaseController.class)
public class KnowledgeBaseControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private KnowledgeBaseService service;

    @Test
    void testGetConcepts() throws Exception {
        ConceptPage page = new ConceptPage();
        page.setTotalElements(1L);
        ConceptItem item = new ConceptItem();
        item.setSlug("test-concept");
        page.setContent(List.of(item));

        Mockito.when(service.getConcepts(eq("tech"), eq(null), eq(null), any(PageRequest.class)))
               .thenReturn(page);

        mockMvc.perform(get("/api/v1/knowledge-base/concepts?category=tech"))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.totalElements").value(1))
               .andExpect(jsonPath("$.content[0].slug").value("test-concept"));
    }

    @Test
    void testCreateConcept() throws Exception {
        ConceptItem item = new ConceptItem();
        item.setSlug("new-concept");
        item.setTitle("New Concept");

        Mockito.when(service.createConcept(any(ConceptCreateRequest.class))).thenReturn(item);

        mockMvc.perform(post("/api/v1/knowledge-base/concepts")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"slug\": \"new-concept\", \"title\": \"New Concept\", \"category\": \"tech\"}"))
               .andExpect(status().isCreated())
               .andExpect(jsonPath("$.slug").value("new-concept"));
    }

    @Test
    void testGetConceptBySlug() throws Exception {
        ConceptDetail detail = new ConceptDetail();
        detail.setSlug("existing");
        detail.setTitle("Existing Concept");

        Mockito.when(service.getConceptBySlug("existing")).thenReturn(detail);

        mockMvc.perform(get("/api/v1/knowledge-base/concepts/existing"))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.slug").value("existing"));
    }

    @Test
    void testGetCategories() throws Exception {
        CategoryList list = new CategoryList(List.of("tech", "science"));
        Mockito.when(service.getCategories()).thenReturn(list);

        mockMvc.perform(get("/api/v1/knowledge-base/categories"))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.categories[0]").value("tech"));
    }
}
