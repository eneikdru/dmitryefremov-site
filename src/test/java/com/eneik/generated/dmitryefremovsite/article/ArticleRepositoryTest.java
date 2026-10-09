package com.eneik.generated.dmitryefremovsite.article;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class ArticleRepositoryTest {

    @Autowired
    private ArticleRepository articleRepository;

    @Test
    void testSaveAndRetrieveRawMarkdownContentWithoutTruncation() {
        String articleId = UUID.randomUUID().toString();
        String slug = "deontic-logic-and-autonomous-systems";
        String title = "Deontic Logic and Autonomous Systems";

        StringBuilder markdownBuilder = new StringBuilder();
        markdownBuilder.append("# Thought Stream: Deontic Logic\n\n");
        markdownBuilder.append("Deontic logic deals with obligations, permissions, and prohibitions.\n\n");
        markdownBuilder.append("```java\n");
        markdownBuilder.append("public class SystemRule {\n");
        markdownBuilder.append("    public static final String MANDATORY = \"OBLIGATORY\";\n");
        markdownBuilder.append("}\n");
        markdownBuilder.append("```\n\n");

        for (int i = 0; i < 500; i++) {
            markdownBuilder.append("Paragraph ").append(i).append(": Deep dive into autonomous processes and theory of constraints. ");
            markdownBuilder.append("Testing untruncated content persistence across relational datastore boundaries.\n\n");
        }

        String rawMarkdownContent = markdownBuilder.toString();

        Article article = new Article(articleId, title, slug, rawMarkdownContent);
        Article savedArticle = articleRepository.save(article);

        assertThat(savedArticle).isNotNull();
        assertThat(savedArticle.getId()).isEqualTo(articleId);

        Optional<Article> retrievedOptional = articleRepository.findById(articleId);
        assertThat(retrievedOptional).isPresent();

        Article retrieved = retrievedOptional.get();
        assertThat(retrieved.getTitle()).isEqualTo(title);
        assertThat(retrieved.getSlug()).isEqualTo(slug);
        assertThat(retrieved.getContent()).isEqualTo(rawMarkdownContent);
        assertThat(retrieved.getContent().length()).isEqualTo(rawMarkdownContent.length());
    }

    @Test
    void testFindBySlug() {
        String articleId = UUID.randomUUID().toString();
        String slug = "theory-of-constraints";
        String title = "Theory of Constraints in Software Architecture";
        String content = "## Theory of Constraints\n\nFocus on system bottlenecks.";

        Article article = new Article(articleId, title, slug, content);
        articleRepository.save(article);

        Optional<Article> retrievedOptional = articleRepository.findBySlug(slug);
        assertThat(retrievedOptional).isPresent();
        assertThat(retrievedOptional.get().getTitle()).isEqualTo(title);
    }
}
