package com.dmitryefremov.site.canon.repository;

import com.dmitryefremov.site.canon.domain.ConceptMediaLink;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository("siteConceptMediaLinkRepository")
public interface ConceptMediaLinkRepository extends JpaRepository<ConceptMediaLink, Long> {

    List<ConceptMediaLink> findByCanonItemId(Long canonItemId);

    List<ConceptMediaLink> findByCanonItemSlug(String slug);

    List<ConceptMediaLink> findByPodcastEpisodeId(Long podcastEpisodeId);

    List<ConceptMediaLink> findByEssayId(Long essayId);

    List<ConceptMediaLink> findBySparkId(Long sparkId);

    List<ConceptMediaLink> findByArticleId(Long articleId);

    List<ConceptMediaLink> findByMediaTypeAndCanonItemId(String mediaType, Long canonItemId);
}
