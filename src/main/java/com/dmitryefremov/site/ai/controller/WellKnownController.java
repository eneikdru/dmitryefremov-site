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
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/.well-known")
public class WellKnownController {

    private final ArticleService articleService;
    private final EssayService essayService;
    private final PodcastEpisodeService podcastEpisodeService;

    public WellKnownController(
            @Qualifier("siteArticleService") ArticleService articleService,
            @Qualifier("siteEssayService") EssayService essayService,
            @Qualifier("sitePodcastEpisodeService") PodcastEpisodeService podcastEpisodeService) {
        this.articleService = articleService;
        this.essayService = essayService;
        this.podcastEpisodeService = podcastEpisodeService;
    }

    @GetMapping(value = "/llms.txt", produces = MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity<String> getLlmsTxt() {
        StringBuilder sb = new StringBuilder();
        sb.append("# Dmitry Efremov Canon Index\n\n");
        sb.append("> Dynamic index of articles, essays, and podcasts.\n\n");

        sb.append("## Articles\n");
        ArticlePageDto articles = articleService.getArticles(0, 100);
        for (ArticleDto article : articles.getContent()) {
            sb.append(String.format("- [%s](/api/v1/articles/%s): %s\n", article.getTitle(), article.getSlug(), article.getSummary() != null ? article.getSummary() : ""));
        }
        sb.append("\n");

        sb.append("## Essays\n");
        EssayPageDto essays = essayService.getEssays(0, 100);
        for (EssayDto essay : essays.getContent()) {
            sb.append(String.format("- [%s](/api/v1/essays/%s): %s\n", essay.getTitle(), essay.getSlug(), essay.getSummary() != null ? essay.getSummary() : ""));
        }
        sb.append("\n");

        sb.append("## Podcasts\n");
        PodcastEpisodePageDto podcasts = podcastEpisodeService.getEpisodes(0, 100);
        for (PodcastEpisodeDto episode : podcasts.getContent()) {
            sb.append(String.format("- [%s](/api/v1/podcasts/%s): %s\n", episode.getTitle(), episode.getSlug(), episode.getThesis() != null ? episode.getThesis() : ""));
        }
        sb.append("\n");

        return ResponseEntity.ok(sb.toString());
    }

    @GetMapping(value = "/ai-agent.json", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, Object>> getAiAgentJson() {
        Map<String, Object> result = new LinkedHashMap<>();

        ArticlePageDto articlesPage = articleService.getArticles(0, 100);
        List<Map<String, String>> articles = articlesPage.getContent().stream()
                .map(a -> Map.of("title", a.getTitle(), "slug", a.getSlug(), "url", "/api/v1/articles/" + a.getSlug()))
                .collect(Collectors.toList());
        result.put("articles", articles);

        EssayPageDto essaysPage = essayService.getEssays(0, 100);
        List<Map<String, String>> essays = essaysPage.getContent().stream()
                .map(e -> Map.of("title", e.getTitle(), "slug", e.getSlug(), "url", "/api/v1/essays/" + e.getSlug()))
                .collect(Collectors.toList());
        result.put("essays", essays);

        PodcastEpisodePageDto podcastsPage = podcastEpisodeService.getEpisodes(0, 100);
        List<Map<String, String>> podcasts = podcastsPage.getContent().stream()
                .map(p -> Map.of("title", p.getTitle(), "slug", p.getSlug(), "url", "/api/v1/podcasts/" + p.getSlug()))
                .collect(Collectors.toList());
        result.put("podcasts", podcasts);

        return ResponseEntity.ok(result);
    }
}
