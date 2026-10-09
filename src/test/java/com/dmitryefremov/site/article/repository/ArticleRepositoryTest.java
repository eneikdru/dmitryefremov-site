package com.dmitryefremov.site.article.repository;

import com.dmitryefremov.site.article.domain.Article;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.jdbc.core.JdbcTemplate;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class ArticleRepositoryTest {

    @Autowired
    private ArticleRepository articleRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    void testFlywayMigrationCreatedArticlesTable() {
        Integer tableCount = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM information_schema.tables WHERE UPPER(table_name) = 'ARTICLES'",
                Integer.class
        );

        assertThat(tableCount).isGreaterThan(0);
    }

    @Test
    void testSaveAndRetrieveArticlePreservesRawMarkdownWithoutTruncation() {
        OffsetDateTime publishedAt = OffsetDateTime.of(2026, 10, 9, 14, 0, 0, 0, ZoneOffset.UTC);

        String rawMarkdownContent = """
                # Деонтическая логика и теория ограничений

                ## Введение
                Ваш код и ваши решения — это то, кто вы есть на самом деле.

                ### Раздел 1: Формальная логика
                Деонтическая логика оперирует операторами обязательности ($O$), разрешения ($P$) и запрета ($F$).

                ```java
                public class DeonticSystem {
                    public boolean isPermitted(Action action) {
                        return !action.isForbidden();
                    }
                }
                ```

                > "Системы, логика мышления и автономные процессы."

                * Пункт 1: Атомарность
                * Пункт 2: Согласованность
                * Пункт 3: Долговечность

                ---
                """ + "A".repeat(5000);

        Article article = new Article(
                "deontic-logic-and-toc",
                "Деонтическая логика и теория ограничений",
                "Краткое описание глубокой статьи о деонтической логике.",
                rawMarkdownContent,
                publishedAt
        );

        Article saved = articleRepository.save(article);
        articleRepository.flush();

        assertThat(saved.getId()).isNotNull();

        Optional<Article> fetchedOptional = articleRepository.findBySlug("deontic-logic-and-toc");
        assertThat(fetchedOptional).isPresent();

        Article fetched = fetchedOptional.get();
        assertThat(fetched.getSlug()).isEqualTo("deontic-logic-and-toc");
        assertThat(fetched.getTitle()).isEqualTo("Деонтическая логика и теория ограничений");
        assertThat(fetched.getContent()).isEqualTo(rawMarkdownContent);
        assertThat(fetched.getContent()).contains("```java");
        assertThat(fetched.getContent()).hasSize(rawMarkdownContent.length());
    }

    @Test
    void testFindAllByPublishedAtNotNullOrderByPublishedAtDesc() {
        OffsetDateTime now = OffsetDateTime.of(2026, 10, 9, 15, 0, 0, 0, ZoneOffset.UTC);

        Article draft = new Article("draft-slug", "Draft Article", "Summary", "Content", null);
        Article older = new Article("older-slug", "Older Article", "Summary", "Content", now.minusDays(5));
        Article newer = new Article("newer-slug", "Newer Article", "Summary", "Content", now.minusDays(1));

        articleRepository.save(draft);
        articleRepository.save(older);
        articleRepository.save(newer);
        articleRepository.flush();

        Page<Article> page = articleRepository.findAllByPublishedAtNotNullOrderByPublishedAtDesc(PageRequest.of(0, 10));

        assertThat(page.getTotalElements()).isEqualTo(2);
        assertThat(page.getContent()).hasSize(2);
        assertThat(page.getContent().get(0).getSlug()).isEqualTo("newer-slug");
        assertThat(page.getContent().get(1).getSlug()).isEqualTo("older-slug");
    }
}
