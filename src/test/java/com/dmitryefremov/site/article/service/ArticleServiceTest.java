package com.dmitryefremov.site.article.service;

import com.dmitryefremov.site.article.domain.Article;
import com.dmitryefremov.site.article.dto.ArticleDto;
import com.dmitryefremov.site.article.dto.ArticlePageDto;
import com.dmitryefremov.site.article.repository.ArticleRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ArticleServiceTest {

    @Mock
    private ArticleRepository articleRepository;

    private ArticleService articleService;

    @BeforeEach
    void setUp() {
        articleService = new ArticleService(articleRepository);
    }

    @Test
    void testGetArticlesReturnsPageDto() {
        OffsetDateTime publishedAt = OffsetDateTime.of(2026, 10, 9, 12, 0, 0, 0, ZoneOffset.UTC);
        Article article = new Article("slug-1", "Title 1", "Summary 1", "# Content 1", publishedAt);

        Page<Article> page = new PageImpl<>(List.of(article), PageRequest.of(0, 20), 1);
        when(articleRepository.findAllByPublishedAtNotNullOrderByPublishedAtDesc(any(Pageable.class))).thenReturn(page);

        ArticlePageDto result = articleService.getArticles(0, 20);

        assertThat(result.getPage()).isEqualTo(0);
        assertThat(result.getSize()).isEqualTo(20);
        assertThat(result.getTotalElements()).isEqualTo(1);
        assertThat(result.getTotalPages()).isEqualTo(1);
        assertThat(result.getContent()).hasSize(1);

        ArticleDto dto = result.getContent().get(0);
        assertThat(dto.getSlug()).isEqualTo("slug-1");
        assertThat(dto.getTitle()).isEqualTo("Title 1");
        assertThat(dto.getContent()).isEqualTo("# Content 1");
        assertThat(dto.getPublishedAt()).isEqualTo(publishedAt);
    }

    @Test
    void testGetArticlesInvalidPageThrowsException() {
        assertThatThrownBy(() -> articleService.getArticles(-1, 20))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Page index must not be less than zero");
    }

    @Test
    void testGetArticlesInvalidSizeThrowsException() {
        assertThatThrownBy(() -> articleService.getArticles(0, 0))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Page size must be between 1 and 100");

        assertThatThrownBy(() -> articleService.getArticles(0, 101))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Page size must be between 1 and 100");
    }

    @Test
    void testGetArticleBySlugFound() {
        OffsetDateTime publishedAt = OffsetDateTime.of(2026, 10, 9, 12, 0, 0, 0, ZoneOffset.UTC);
        Article article = new Article("deontic-logic", "Deontic Logic", "Summary", "# Content markdown", publishedAt);

        when(articleRepository.findBySlug("deontic-logic")).thenReturn(Optional.of(article));

        Optional<ArticleDto> result = articleService.getArticleBySlug("deontic-logic");

        assertThat(result).isPresent();
        assertThat(result.get().getSlug()).isEqualTo("deontic-logic");
        assertThat(result.get().getTitle()).isEqualTo("Deontic Logic");
        assertThat(result.get().getContent()).isEqualTo("# Content markdown");
    }

    @Test
    void testGetArticleBySlugNotFound() {
        when(articleRepository.findBySlug("unknown")).thenReturn(Optional.empty());

        Optional<ArticleDto> result = articleService.getArticleBySlug("unknown");

        assertThat(result).isEmpty();
    }
}
