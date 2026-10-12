package com.dmitryefremov.site.essay.controller;

import com.dmitryefremov.site.essay.dto.EssayDto;
import com.dmitryefremov.site.essay.dto.EssayPageDto;
import com.dmitryefremov.site.essay.service.EssayService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(EssayController.class)
class EssayControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean(name = "siteEssayService")
    private EssayService essayService;

    @Test
    void testListEssaysReturns200() throws Exception {
        OffsetDateTime publishedAt = OffsetDateTime.of(2026, 10, 12, 10, 0, 0, 0, ZoneOffset.UTC);
        EssayDto essay = new EssayDto(
                1L,
                "editorial-essay-1",
                "Editorial Essay",
                "Essay summary",
                "# High-End Editorial Content\n\nDeep analysis of deontic logic.",
                publishedAt,
                publishedAt
        );

        EssayPageDto pageDto = new EssayPageDto(List.of(essay), 0, 20, 1L, 1);

        when(essayService.getEssays(0, 20)).thenReturn(pageDto);

        mockMvc.perform(get("/api/v1/essays")
                        .param("page", "0")
                        .param("size", "20")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.page", is(0)))
                .andExpect(jsonPath("$.size", is(20)))
                .andExpect(jsonPath("$.totalElements", is(1)))
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].slug", is("editorial-essay-1")))
                .andExpect(jsonPath("$.content[0].title", is("Editorial Essay")))
                .andExpect(jsonPath("$.content[0].summary", is("Essay summary")));
    }

    @Test
    void testGetEssayBySlugReturns200WithFullEssayData() throws Exception {
        OffsetDateTime publishedAt = OffsetDateTime.of(2026, 10, 12, 10, 0, 0, 0, ZoneOffset.UTC);
        String markdown = "# Deontic Logic Essay\n\nSystematic exploration of deontic modalities.";

        EssayDto essay = new EssayDto(
                1L,
                "deontic-logic-essay",
                "Deontic Logic Essay",
                "Summary of deontic logic essay",
                markdown,
                publishedAt,
                publishedAt
        );

        when(essayService.getEssayBySlug("deontic-logic-essay"))
                .thenReturn(Optional.of(essay));

        mockMvc.perform(get("/api/v1/essays/deontic-logic-essay")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.slug", is("deontic-logic-essay")))
                .andExpect(jsonPath("$.title", is("Deontic Logic Essay")))
                .andExpect(jsonPath("$.summary", is("Summary of deontic logic essay")))
                .andExpect(jsonPath("$.content", is(markdown)));
    }

    @Test
    void testGetEssayBySlugNotFoundReturns404() throws Exception {
        when(essayService.getEssayBySlug("non-existent-essay"))
                .thenReturn(Optional.empty());

        mockMvc.perform(get("/api/v1/essays/non-existent-essay")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status", is(404)))
                .andExpect(jsonPath("$.message", is("Essay with slug 'non-existent-essay' was not found")));
    }

    @Test
    void testListEssaysInvalidPaginationReturns400() throws Exception {
        when(essayService.getEssays(anyInt(), anyInt()))
                .thenThrow(new IllegalArgumentException("Page size must be between 1 and 100"));

        mockMvc.perform(get("/api/v1/essays")
                        .param("page", "0")
                        .param("size", "150")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status", is(400)))
                .andExpect(jsonPath("$.message", is("Page size must be between 1 and 100")));
    }
}
