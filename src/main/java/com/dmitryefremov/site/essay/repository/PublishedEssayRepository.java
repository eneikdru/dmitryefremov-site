package com.dmitryefremov.site.essay.repository;

import com.dmitryefremov.site.essay.domain.PublishedEssay;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PublishedEssayRepository extends JpaRepository<PublishedEssay, Long> {

    Optional<PublishedEssay> findBySlug(String slug);

    Page<PublishedEssay> findAllByOrderByPublishedAtDesc(Pageable pageable);
}
