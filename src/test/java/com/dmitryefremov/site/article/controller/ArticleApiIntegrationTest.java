package com.dmitryefremov.site.article.controller;

import com.dmitryefremov.site.article.domain.Article;
import com.dmitryefremov.site.article.repository.ArticleRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class ArticleApiIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ArticleRepository articleRepository;

    private OffsetDateTime fixedTime;

    @BeforeEach
    void setUp() {
        articleRepository.deleteAll();
        articleRepository.flush();
        fixedTime = OffsetDateTime.of(2026, 10, 9, 12, 0, 0, 0, ZoneOffset.UTC);

        Article article1 = new Article(
                "integration-test-deontic-logic",
                "Deontic Logic in Autonomous Process Architecture",
                "Exploring normative modal logic as a foundational model.",
                "# Deontic Logic\n\n$$\\mathcal{O}(\\varphi) \\iff \\neg \\mathcal{P}(\\neg \\varphi)$$\n\n> Quote",
                fixedTime.minusHours(2)
        );

        Article article2 = new Article(
                "integration-test-theory-of-constraints",
                "Theory of Constraints in Distributed Pipeline Throughput",
                "Applying TOC and Five Focusing Steps to software delivery loops.",
                "# Theory of Constraints\n\n$$\\text{Throughput} = \\frac{\\text{Work}}{\\text{Time}}$$",
                fixedTime.minusHours(1)
        );

        articleRepository.save(article1);
        articleRepository.save(article2);
        articleRepository.flush();
    }

    @Test
    void testListArticlesReturnsAllStoredArticlesAccurately() throws Exception {
        mockMvc.perform(get("/api/v1/articles")
                        .param("page", "0")
                        .param("size", "10")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.page", is(0)))
                .andExpect(jsonPath("$.size", is(10)))
                .andExpect(jsonPath("$.totalElements", is(2)))
                .andExpect(jsonPath("$.totalPages", is(1)))
                .andExpect(jsonPath("$.content", hasSize(2)))
                .andExpect(jsonPath("$.content[0].slug", is("integration-test-theory-of-constraints")))
                .andExpect(jsonPath("$.content[0].title", is("Theory of Constraints in Distributed Pipeline Throughput")))
                .andExpect(jsonPath("$.content[1].slug", is("integration-test-deontic-logic")))
                .andExpect(jsonPath("$.content[1].title", is("Deontic Logic in Autonomous Process Architecture")));
    }

    @Test
    void testGetArticleBySlugReturnsAccurateStoredArticleContent() throws Exception {
        mockMvc.perform(get("/api/v1/articles/integration-test-deontic-logic")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.slug", is("integration-test-deontic-logic")))
                .andExpect(jsonPath("$.title", is("Deontic Logic in Autonomous Process Architecture")))
                .andExpect(jsonPath("$.summary", is("Exploring normative modal logic as a foundational model.")))
                .andExpect(jsonPath("$.content", is("# Deontic Logic\n\n$$\\mathcal{O}(\\varphi) \\iff \\neg \\mathcal{P}(\\neg \\varphi)$$\n\n> Quote")));
    }

    @Test
    void testGetArticleBySlugNotFound() throws Exception {
        mockMvc.perform(get("/api/v1/articles/unknown-slug")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status", is(404)))
                .andExpect(jsonPath("$.message", is("Article with slug 'unknown-slug' was not found")));
    }
}
