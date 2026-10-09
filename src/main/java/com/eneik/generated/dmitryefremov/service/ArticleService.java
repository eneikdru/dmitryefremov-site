package com.eneik.generated.dmitryefremov.service;

import com.eneik.generated.dmitryefremov.model.Article;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class ArticleService {
    private final ConcurrentHashMap<String, Article> articles = new ConcurrentHashMap<>();

    public ArticleService() {
        Article sample = new Article(
            "art-1",
            "systems-logic",
            "Deontic Logic in Autonomous Systems",
            "An exploration of deontic logic principles and application in software architectures.",
            "# Deontic Logic in Autonomous Systems\n\nSystems engineering requires formal guarantees...",
            Instant.parse("2026-01-15T10:00:00Z")
        );
        articles.put(sample.getSlug(), sample);
    }

    public List<Article> getAllArticles() {
        return new ArrayList<>(articles.values());
    }

    public Optional<Article> findBySlug(String slug) {
        return Optional.ofNullable(articles.get(slug));
    }

    public void addArticle(Article article) {
        articles.put(article.getSlug(), article);
    }
}
