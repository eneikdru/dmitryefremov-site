package com.dmitryefremov.site.canon;

import com.dmitryefremov.site.article.domain.Article;
import com.dmitryefremov.site.article.repository.ArticleRepository;
import com.dmitryefremov.site.canon.domain.CanonDomain;
import com.dmitryefremov.site.canon.domain.CanonItem;
import com.dmitryefremov.site.canon.domain.CanonType;
import com.dmitryefremov.site.canon.domain.ConceptMediaLink;
import com.dmitryefremov.site.canon.repository.CanonItemRepository;
import com.dmitryefremov.site.canon.repository.ConceptMediaLinkRepository;
import com.dmitryefremov.site.essay.domain.Essay;
import com.dmitryefremov.site.essay.repository.EssayRepository;
import com.dmitryefremov.site.podcast.domain.PodcastEpisode;
import com.dmitryefremov.site.podcast.repository.PodcastEpisodeRepository;
import com.dmitryefremov.site.spark.domain.Spark;
import com.dmitryefremov.site.spark.repository.SparkRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.jdbc.core.JdbcTemplate;

import java.time.Clock;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class ConceptGraphSchemaIntegrationTest {

    @Autowired
    private ConceptMediaLinkRepository conceptMediaLinkRepository;

    @Autowired
    private CanonItemRepository canonItemRepository;

    @Autowired
    private PodcastEpisodeRepository podcastEpisodeRepository;

    @Autowired
    private EssayRepository essayRepository;

    @Autowired
    private SparkRepository sparkRepository;

    @Autowired
    private ArticleRepository articleRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    void testConceptMediaLinksTableAndIndexesCreated() {
        Integer count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM information_schema.tables WHERE UPPER(table_name) = 'CONCEPT_MEDIA_LINKS'",
                Integer.class
        );
        assertThat(count).isGreaterThan(0);

        List<String> indexes = jdbcTemplate.queryForList(
                "SELECT UPPER(index_name) FROM information_schema.indexes WHERE UPPER(table_name) = 'CONCEPT_MEDIA_LINKS'",
                String.class
        );

        assertThat(indexes).contains(
                "IDX_CONCEPT_MEDIA_CANON_ID",
                "IDX_CONCEPT_MEDIA_PODCAST_ID",
                "IDX_CONCEPT_MEDIA_ESSAY_ID",
                "IDX_CONCEPT_MEDIA_SPARK_ID",
                "IDX_CONCEPT_MEDIA_ARTICLE_ID",
                "IDX_CONCEPT_MEDIA_TYPE"
        );
    }

    @Test
    void testSeededConceptMediaLinksArePresent() {
        List<ConceptMediaLink> links = conceptMediaLinkRepository.findAll();
        assertThat(links).isNotEmpty();
    }

    @Test
    void testBidirectionalGraphPersistenceAndQuerying() {
        Clock fixedClock = Clock.fixed(OffsetDateTime.of(2026, 10, 24, 12, 0, 0, 0, ZoneOffset.UTC).toInstant(), ZoneOffset.UTC);

        CanonItem canonItem = canonItemRepository.save(new CanonItem(
                "test-concept-graph-node",
                "Test Concept Node",
                CanonType.ONTOLOGY,
                CanonDomain.PHILOSOPHY_OF_MIND,
                "Summary of graph node",
                "# Test Concept Node\n\nContent...",
                OffsetDateTime.now(fixedClock),
                fixedClock
        ));

        PodcastEpisode episode = podcastEpisodeRepository.save(new PodcastEpisode(
                "test-podcast-node",
                "Test Podcast Node",
                "Thesis...",
                "Description...",
                "https://audio.url/1.mp3",
                "https://media.link/1",
                1800,
                OffsetDateTime.now(fixedClock)
        ));

        Essay essay = essayRepository.save(new Essay(
                "test-essay-node",
                "Test Essay Node",
                "Summary...",
                "Content...",
                OffsetDateTime.now(fixedClock)
        ));

        Spark spark = sparkRepository.save(new Spark(
                99001L,
                99001L,
                "Test Spark Node content",
                "PHILOSOPHY_OF_MIND",
                "#test #graph",
                "@author",
                OffsetDateTime.now(fixedClock)
        ));

        Article article = articleRepository.save(new Article(
                "test-article-node",
                "Test Article Node",
                "Summary...",
                "Content...",
                OffsetDateTime.now(fixedClock)
        ));

        // Create bidirectional graph links
        ConceptMediaLink linkPodcast = new ConceptMediaLink(canonItem, "PODCAST", "EXPLAINS", "Explains concept in podcast", fixedClock);
        linkPodcast.setPodcastEpisode(episode);

        ConceptMediaLink linkEssay = new ConceptMediaLink(canonItem, "ESSAY", "CITES", "Cites concept in essay", fixedClock);
        linkEssay.setEssay(essay);

        ConceptMediaLink linkSpark = new ConceptMediaLink(canonItem, "SPARK", "DISCUSSES", "Brief spark discussing concept", fixedClock);
        linkSpark.setSpark(spark);

        ConceptMediaLink linkArticle = new ConceptMediaLink(canonItem, "ARTICLE", "REFERENCES", "References concept in article", fixedClock);
        linkArticle.setArticle(article);

        conceptMediaLinkRepository.saveAll(List.of(linkPodcast, linkEssay, linkSpark, linkArticle));
        conceptMediaLinkRepository.flush();

        // Query 1: From Canon Node to Media Items
        List<ConceptMediaLink> canonLinks = conceptMediaLinkRepository.findByCanonItemId(canonItem.getId());
        assertThat(canonLinks).hasSize(4);

        // Query 2: From Podcast Episode to Canon Node
        List<ConceptMediaLink> podcastLinks = conceptMediaLinkRepository.findByPodcastEpisodeId(episode.getId());
        assertThat(podcastLinks).hasSize(1);
        assertThat(podcastLinks.get(0).getCanonItem().getSlug()).isEqualTo("test-concept-graph-node");

        // Query 3: From Essay to Canon Node
        List<ConceptMediaLink> essayLinks = conceptMediaLinkRepository.findByEssayId(essay.getId());
        assertThat(essayLinks).hasSize(1);
        assertThat(essayLinks.get(0).getCanonItem().getSlug()).isEqualTo("test-concept-graph-node");

        // Query 4: From Spark to Canon Node
        List<ConceptMediaLink> sparkLinks = conceptMediaLinkRepository.findBySparkId(spark.getId());
        assertThat(sparkLinks).hasSize(1);
        assertThat(sparkLinks.get(0).getCanonItem().getSlug()).isEqualTo("test-concept-graph-node");

        // Query 5: From Article to Canon Node
        List<ConceptMediaLink> articleLinks = conceptMediaLinkRepository.findByArticleId(article.getId());
        assertThat(articleLinks).hasSize(1);
        assertThat(articleLinks.get(0).getCanonItem().getSlug()).isEqualTo("test-concept-graph-node");
    }

    @Test
    void testCascadeDeleteWhenCanonItemDeleted() {
        Clock fixedClock = Clock.fixed(OffsetDateTime.of(2026, 10, 24, 12, 0, 0, 0, ZoneOffset.UTC).toInstant(), ZoneOffset.UTC);

        CanonItem item = canonItemRepository.save(new CanonItem(
                "deletable-concept-node",
                "Deletable Concept Node",
                CanonType.GLOSSARY_TERM,
                CanonDomain.AUTONOMOUS_ENGINEERING,
                "Summary...",
                "Content...",
                OffsetDateTime.now(fixedClock),
                fixedClock
        ));

        ConceptMediaLink link = new ConceptMediaLink(item, "PODCAST", "DISCUSSES", "Link to be deleted", fixedClock);
        conceptMediaLinkRepository.save(link);
        conceptMediaLinkRepository.flush();

        canonItemRepository.delete(item);
        canonItemRepository.flush();

        List<ConceptMediaLink> remainingLinks = conceptMediaLinkRepository.findByCanonItemId(item.getId());
        assertThat(remainingLinks).isEmpty();
    }
}
