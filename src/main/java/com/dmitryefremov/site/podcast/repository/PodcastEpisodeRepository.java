package com.dmitryefremov.site.podcast.repository;

import com.dmitryefremov.site.podcast.domain.PodcastEpisode;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PodcastEpisodeRepository extends JpaRepository<PodcastEpisode, Long> {
    Optional<PodcastEpisode> findBySlug(String slug);
    Page<PodcastEpisode> findAllByOrderByPublishedAtDesc(Pageable pageable);
}
