package com.dmitryefremov.site.canon.service;

import com.dmitryefremov.site.article.domain.Article;
import com.dmitryefremov.site.article.repository.ArticleRepository;
import com.dmitryefremov.site.canon.domain.CanonItem;
import com.dmitryefremov.site.canon.dto.CorpusSearchDto;
import com.dmitryefremov.site.canon.dto.SearchResultItemDto;
import com.dmitryefremov.site.canon.repository.CanonItemRepository;
import com.dmitryefremov.site.essay.domain.Essay;
import com.dmitryefremov.site.essay.repository.EssayRepository;
import com.dmitryefremov.site.podcast.domain.PodcastEpisode;
import com.dmitryefremov.site.podcast.repository.PodcastEpisodeRepository;
import com.dmitryefremov.site.spark.domain.Spark;
import com.dmitryefremov.site.spark.repository.SparkRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

@Service("siteCorpusSearchService")
@Transactional(readOnly = true)
public class CorpusSearchService {

    private final CanonItemRepository canonItemRepository;
    private final PodcastEpisodeRepository podcastEpisodeRepository;
    private final EssayRepository essayRepository;
    private final SparkRepository sparkRepository;
    private final ArticleRepository articleRepository;

    public CorpusSearchService(@Qualifier("siteCanonItemRepository") CanonItemRepository canonItemRepository,
                               PodcastEpisodeRepository podcastEpisodeRepository,
                               EssayRepository essayRepository,
                               SparkRepository sparkRepository,
                               ArticleRepository articleRepository) {
        this.canonItemRepository = canonItemRepository;
        this.podcastEpisodeRepository = podcastEpisodeRepository;
        this.essayRepository = essayRepository;
        this.sparkRepository = sparkRepository;
        this.articleRepository = articleRepository;
    }

    public CorpusSearchDto searchCorpus(String query) {
        if (query == null || query.isBlank()) {
            return new CorpusSearchDto("", 0, List.of());
        }

        String normalizedQuery = query.toLowerCase(Locale.ROOT).trim();
        List<SearchResultItemDto> results = new ArrayList<>();

        // 1. Search Canon items
        List<CanonItem> canonItems = canonItemRepository.findAll();
        for (CanonItem item : canonItems) {
            double score = computeMatchScore(normalizedQuery, item.getTitle(), item.getSummary(), item.getContent());
            if (score > 0) {
                results.add(new SearchResultItemDto(
                        "canon-" + item.getSlug(),
                        item.getTitle(),
                        item.getSlug(),
                        "CANON",
                        item.getSummary(),
                        generateSnippet(item.getContent(), normalizedQuery),
                        "/canon/" + item.getSlug(),
                        score,
                        item.getPublishedAt()
                ));
            }
        }

        // 2. Search Podcast episodes
        List<PodcastEpisode> episodes = podcastEpisodeRepository.findAll();
        for (PodcastEpisode ep : episodes) {
            double score = computeMatchScore(normalizedQuery, ep.getTitle(), ep.getThesis(), ep.getDescription());
            if (score > 0) {
                results.add(new SearchResultItemDto(
                        "podcast-" + ep.getSlug(),
                        ep.getTitle(),
                        ep.getSlug(),
                        "PODCAST",
                        ep.getThesis(),
                        generateSnippet(ep.getDescription(), normalizedQuery),
                        "/podcasts/" + ep.getSlug(),
                        score,
                        ep.getPublishedAt()
                ));
            }
        }

        // 3. Search Essays
        List<Essay> essays = essayRepository.findAll();
        for (Essay essay : essays) {
            double score = computeMatchScore(normalizedQuery, essay.getTitle(), essay.getSummary(), essay.getContent());
            if (score > 0) {
                results.add(new SearchResultItemDto(
                        "essay-" + essay.getSlug(),
                        essay.getTitle(),
                        essay.getSlug(),
                        "ESSAY",
                        essay.getSummary(),
                        generateSnippet(essay.getContent(), normalizedQuery),
                        "/essays/" + essay.getSlug(),
                        score,
                        essay.getPublishedAt()
                ));
            }
        }

        // 4. Search Sparks
        List<Spark> sparks = sparkRepository.findAll();
        for (Spark spark : sparks) {
            double score = computeMatchScore(normalizedQuery, spark.getDomain(), spark.getTags(), spark.getContent());
            if (score > 0) {
                results.add(new SearchResultItemDto(
                        "spark-" + spark.getId(),
                        "Spark: " + (spark.getDomain() != null ? spark.getDomain() : "Note"),
                        String.valueOf(spark.getId()),
                        "SPARK",
                        spark.getContent(),
                        generateSnippet(spark.getContent(), normalizedQuery),
                        "/sparks/" + spark.getId(),
                        score,
                        spark.getCreatedAt()
                ));
            }
        }

        // 5. Search Articles
        List<Article> articles = articleRepository.findAll();
        for (Article art : articles) {
            double score = computeMatchScore(normalizedQuery, art.getTitle(), art.getSummary(), art.getContent());
            if (score > 0) {
                results.add(new SearchResultItemDto(
                        "article-" + art.getSlug(),
                        art.getTitle(),
                        art.getSlug(),
                        "ARTICLE",
                        art.getSummary(),
                        generateSnippet(art.getContent(), normalizedQuery),
                        "/articles/" + art.getSlug(),
                        score,
                        art.getPublishedAt()
                ));
            }
        }

        // Sort by score descending, then title ascending
        results.sort(Comparator.comparing(SearchResultItemDto::getScore, Comparator.reverseOrder())
                .thenComparing(SearchResultItemDto::getTitle, Comparator.nullsLast(Comparator.naturalOrder())));

        return new CorpusSearchDto(query, results.size(), results);
    }

    private double computeMatchScore(String query, String title, String summary, String body) {
        double score = 0.0;
        if (title != null && title.toLowerCase(Locale.ROOT).contains(query)) {
            score += 10.0;
        }
        if (summary != null && summary.toLowerCase(Locale.ROOT).contains(query)) {
            score += 5.0;
        }
        if (body != null && body.toLowerCase(Locale.ROOT).contains(query)) {
            score += 2.0;
        }
        return score;
    }

    private String generateSnippet(String text, String query) {
        if (text == null || text.isBlank()) {
            return "";
        }
        int idx = text.toLowerCase(Locale.ROOT).indexOf(query);
        if (idx == -1) {
            return text.length() > 150 ? text.substring(0, 150) + "..." : text;
        }
        int start = Math.max(0, idx - 40);
        int end = Math.min(text.length(), idx + query.length() + 60);
        String prefix = start > 0 ? "..." : "";
        String suffix = end < text.length() ? "..." : "";
        return prefix + text.substring(start, end).trim() + suffix;
    }
}
