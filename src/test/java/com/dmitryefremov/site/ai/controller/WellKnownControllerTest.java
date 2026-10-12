package com.dmitryefremov.site.ai.controller;

import com.dmitryefremov.site.article.dto.ArticleDto;
import com.dmitryefremov.site.article.dto.ArticlePageDto;
import com.dmitryefremov.site.article.service.ArticleService;
import com.dmitryefremov.site.essay.dto.EssayDto;
import com.dmitryefremov.site.essay.dto.EssayPageDto;
import com.dmitryefremov.site.essay.service.EssayService;
import com.dmitryefremov.site.podcast.dto.PodcastEpisodeDto;
import com.dmitryefremov.site.podcast.dto.PodcastEpisodePageDto;
import com.dmitryefremov.site.podcast.service.PodcastEpisodeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.OffsetDateTime;
import java.util.List;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(WellKnownController.class)
class WellKnownControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean(name = "siteArticleService")
    private ArticleService articleService;

    @MockBean(name = "siteEssayService")
    private EssayService essayService;

    @MockBean(name = "sitePodcastEpisodeService")
    private PodcastEpisodeService podcastEpisodeService;

    @BeforeEach
    void setUp() {
        ArticleDto article1 = new ArticleDto(1L, "article-slug-1", "Article Title 1", "Article Summary 1", "Content 1", OffsetDateTime.now(), OffsetDateTime.now());
        ArticleDto article2 = new ArticleDto(2L, "article-slug-2", "Article Title 2", null, "Content 2", OffsetDateTime.now(), OffsetDateTime.now());
        ArticlePageDto articlePage = new ArticlePageDto(List.of(article1, article2), 0, 100, 2, 1);
        when(articleService.getArticles(anyInt(), anyInt())).thenReturn(articlePage);

        EssayDto essay = new EssayDto(1L, "essay-slug", "Essay Title", "Essay Summary", "Content", OffsetDateTime.now(), OffsetDateTime.now());
        EssayPageDto essayPage = new EssayPageDto(List.of(essay), 0, 100, 1, 1);
        when(essayService.getEssays(anyInt(), anyInt())).thenReturn(essayPage);

        PodcastEpisodeDto podcast = new PodcastEpisodeDto(1L, "podcast-slug", "Podcast Title", "Podcast Thesis", "Description", "url", "link", "yt", "tg", "sp", "ap", "platforms", 3600, OffsetDateTime.now(), OffsetDateTime.now(), List.of());
        PodcastEpisodePageDto podcastPage = new PodcastEpisodePageDto(List.of(podcast), 0, 100, 1, 1);
        when(podcastEpisodeService.getEpisodes(anyInt(), anyInt())).thenReturn(podcastPage);
    }

    @Test
    void testGetLlmsTxtMarkdownFormat() throws Exception {
        mockMvc.perform(get("/.well-known/llms.txt"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_PLAIN))
                .andExpect(content().string(containsString("# Dmitry Efremov Canon Index")))
                .andExpect(content().string(containsString("> Dynamic index of articles, essays, and podcasts.")))
                .andExpect(content().string(containsString("## Articles")))
                .andExpect(content().string(containsString("- [Article Title 1](/api/v1/articles/article-slug-1): Article Summary 1")))
                .andExpect(content().string(containsString("- [Article Title 2](/api/v1/articles/article-slug-2): ")))
                .andExpect(content().string(containsString("## Essays")))
                .andExpect(content().string(containsString("- [Essay Title](/api/v1/essays/essay-slug): Essay Summary")))
                .andExpect(content().string(containsString("## Podcasts")))
                .andExpect(content().string(containsString("- [Podcast Title](/api/v1/podcasts/podcast-slug): Podcast Thesis")));
    }

    @Test
    void testGetAiAgentJsonStructure() throws Exception {
        mockMvc.perform(get("/.well-known/ai-agent.json"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.articles", hasSize(2)))
                .andExpect(jsonPath("$.articles[0].title").value("Article Title 1"))
                .andExpect(jsonPath("$.articles[0].slug").value("article-slug-1"))
                .andExpect(jsonPath("$.articles[0].url").value("/api/v1/articles/article-slug-1"))
                .andExpect(jsonPath("$.articles[1].title").value("Article Title 2"))
                .andExpect(jsonPath("$.essays", hasSize(1)))
                .andExpect(jsonPath("$.essays[0].title").value("Essay Title"))
                .andExpect(jsonPath("$.essays[0].slug").value("essay-slug"))
                .andExpect(jsonPath("$.essays[0].url").value("/api/v1/essays/essay-slug"))
                .andExpect(jsonPath("$.podcasts", hasSize(1)))
                .andExpect(jsonPath("$.podcasts[0].title").value("Podcast Title"))
                .andExpect(jsonPath("$.podcasts[0].slug").value("podcast-slug"))
                .andExpect(jsonPath("$.podcasts[0].url").value("/api/v1/podcasts/podcast-slug"));
    }

    @Test
    void testGetAiAgentJsonAndLlmsTxtWithEmptyContent() throws Exception {
        when(articleService.getArticles(anyInt(), anyInt())).thenReturn(new ArticlePageDto(List.of(), 0, 100, 0, 0));
        when(essayService.getEssays(anyInt(), anyInt())).thenReturn(new EssayPageDto(List.of(), 0, 100, 0, 0));
        when(podcastEpisodeService.getEpisodes(anyInt(), anyInt())).thenReturn(new PodcastEpisodePageDto(List.of(), 0, 100, 0, 0));

        mockMvc.perform(get("/.well-known/ai-agent.json"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.articles", hasSize(0)))
                .andExpect(jsonPath("$.essays", hasSize(0)))
                .andExpect(jsonPath("$.podcasts", hasSize(0)));

        mockMvc.perform(get("/.well-known/llms.txt"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("# Dmitry Efremov Canon Index")));
    }
}
