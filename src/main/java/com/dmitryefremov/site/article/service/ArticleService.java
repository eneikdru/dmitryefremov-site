package com.dmitryefremov.site.article.service;

import com.dmitryefremov.site.article.domain.Article;
import com.dmitryefremov.site.article.dto.ArticleDto;
import com.dmitryefremov.site.article.dto.ArticlePageDto;
import com.dmitryefremov.site.article.repository.ArticleRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service("siteArticleService")
@Transactional(readOnly = true)
public class ArticleService {

    private final ArticleRepository articleRepository;

    public ArticleService(ArticleRepository articleRepository) {
        this.articleRepository = articleRepository;
    }

    public ArticlePageDto getArticles(int page, int size) {
        if (page < 0) {
            throw new IllegalArgumentException("Page index must not be less than zero");
        }
        if (size < 1 || size > 100) {
            throw new IllegalArgumentException("Page size must be between 1 and 100");
        }

        Pageable pageable = PageRequest.of(page, size);
        Page<Article> articlePage = articleRepository.findAllByPublishedAtNotNullOrderByPublishedAtDesc(pageable);

        List<ArticleDto> dtos = articlePage.getContent().stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());

        return new ArticlePageDto(
                dtos,
                articlePage.getNumber(),
                articlePage.getSize(),
                articlePage.getTotalElements(),
                articlePage.getTotalPages()
        );
    }

    public Optional<ArticleDto> getArticleBySlug(String slug) {
        return articleRepository.findBySlug(slug)
                .map(this::mapToDto);
    }

    public ArticleDto mapToDto(Article entity) {
        return new ArticleDto(
                entity.getId(),
                entity.getSlug(),
                entity.getTitle(),
                entity.getSummary(),
                entity.getContent(),
                entity.getPublishedAt(),
                entity.getCreatedAt()
        );
    }
}
