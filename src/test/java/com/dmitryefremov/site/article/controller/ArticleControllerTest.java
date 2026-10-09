package com.dmitryefremov.site.article.controller;

import com.dmitryefremov.site.article.dto.ArticleDto;
import com.dmitryefremov.site.article.dto.ArticlePageDto;
import com.dmitryefremov.site.article.service.ArticleService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
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

@WebMvcTest(ArticleController.class)
class ArticleControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean(name = "siteArticleService")
    private ArticleService articleService;

    @Test
    void testListArticlesReturns200() throws Exception {
        OffsetDateTime publishedAt = OffsetDateTime.of(2026, 10, 9, 12, 0, 0, 0, ZoneOffset.UTC);
        ArticleDto article = new ArticleDto(
                1L,
                "thought-stream-1",
                "Thought Stream Article",
                "Article summary",
                "# Full Markdown Longread\n\nThought stream content.",
                publishedAt,
                publishedAt
        );

        ArticlePageDto pageDto = new ArticlePageDto(List.of(article), 0, 20, 1L, 1);

        when(articleService.getArticles(0, 20)).thenReturn(pageDto);

        mockMvc.perform(get("/api/v1/articles")
                        .param("page", "0")
                        .param("size", "20")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.page", is(0)))
                .andExpect(jsonPath("$.size", is(20)))
                .andExpect(jsonPath("$.totalElements", is(1)))
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].slug", is("thought-stream-1")))
                .andExpect(jsonPath("$.content[0].title", is("Thought Stream Article")))
                .andExpect(jsonPath("$.content[0].summary", is("Article summary")));
    }

    @Test
    void testGetArticleBySlugReturns200WithFullMarkdownContent() throws Exception {
        OffsetDateTime publishedAt = OffsetDateTime.of(2026, 10, 9, 12, 0, 0, 0, ZoneOffset.UTC);
        String markdown = "# Longread\n\nDeep thoughts on deontic logic and theory of constraints.";

        ArticleDto article = new ArticleDto(
                1L,
                "deontic-logic",
                "Deontic Logic Longread",
                "Deontic logic summary",
                markdown,
                publishedAt,
                publishedAt
        );

        when(articleService.getArticleBySlug("deontic-logic"))
                .thenReturn(Optional.of(article));

        mockMvc.perform(get("/api/v1/articles/deontic-logic")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.slug", is("deontic-logic")))
                .andExpect(jsonPath("$.title", is("Deontic Logic Longread")))
                .andExpect(jsonPath("$.summary", is("Deontic logic summary")))
                .andExpect(jsonPath("$.content", is(markdown)));
    }

    @Test
    void testGetArticleBySlugNotFoundReturns404() throws Exception {
        when(articleService.getArticleBySlug("non-existent-article"))
                .thenReturn(Optional.empty());

        mockMvc.perform(get("/api/v1/articles/non-existent-article")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status", is(404)))
                .andExpect(jsonPath("$.message", is("Article with slug 'non-existent-article' was not found")));
    }

    @Test
    void testListArticlesInvalidPaginationReturns400() throws Exception {
        when(articleService.getArticles(anyInt(), anyInt()))
                .thenThrow(new IllegalArgumentException("Page size must be between 1 and 100"));

        mockMvc.perform(get("/api/v1/articles")
                        .param("page", "0")
                        .param("size", "150")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status", is(400)))
                .andExpect(jsonPath("$.message", is("Page size must be between 1 and 100")));
    }
}
